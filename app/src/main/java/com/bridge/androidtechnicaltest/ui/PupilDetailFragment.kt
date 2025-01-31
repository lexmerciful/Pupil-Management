package com.bridge.androidtechnicaltest.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.databinding.FragmentPupildetailBinding
import com.bridge.androidtechnicaltest.db.Pupil
import com.bridge.androidtechnicaltest.util.Constants.UPDATE_PUPIL_KEY
import com.bridge.androidtechnicaltest.util.Constants.EDIT_PUPIL_KEY
import com.bridge.androidtechnicaltest.util.Constants.IS_DELETE_SUCCESSFUL
import com.bridge.androidtechnicaltest.util.Constants.IS_EDIT
import com.bridge.androidtechnicaltest.util.Constants.IS_UPDATE_SUCCESSFUL
import com.bridge.androidtechnicaltest.util.Constants.PUPIL
import com.bridge.androidtechnicaltest.util.Constants.showAlertDialog
import com.bridge.androidtechnicaltest.util.Resource
import com.bridge.androidtechnicaltest.viewmodel.PupilViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PupilDetailFragment : Fragment() {

    private lateinit var binding: FragmentPupildetailBinding
    private val pupilViewModel by viewModels<PupilViewModel>()
    lateinit var pupil: Pupil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPupildetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupToolbar()

        setViewData()

        setupEditPupil()

        setupDeletePupil()

        setupFragmentResultListener()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.pupilDetailToolbar)

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Pupil Details"
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun init() {
        if (arguments?.getString(PUPIL) != null) {
            pupil = Gson().fromJson(arguments?.getString(PUPIL), Pupil::class.java)
        }
    }

    private fun setViewData(){
        binding.pupilPhotoImageView.load(pupil.image) {
            placeholder(R.drawable.ic_profile_placeholder)
            error(R.drawable.ic_profile_placeholder)
            crossfade(true)
        }

        binding.pupilIdTextView.text = pupil.pupilId.toString()

        binding.pupilNameTextView.text = pupil.name

        binding.pupilCountryTextView.text = pupil.country
    }

    private fun setupEditPupil() {
        val addPupilFragment = AddPupilFragment()

        val bundle = bundleOf(PUPIL to Gson().toJson(pupil), IS_EDIT to true)
        addPupilFragment.arguments = bundle

        binding.editButton.setOnClickListener {
            val fm = requireActivity().supportFragmentManager
            val tran = fm.beginTransaction().apply {
                hide(this@PupilDetailFragment)
                add(R.id.container, addPupilFragment)
                addToBackStack(null)
            }
            tran.commit()
        }
    }

    private fun setupDeletePupil() {
        binding.deleteButton.setOnClickListener {
            showAlertDialog(
                requireContext(),
                getString(R.string.delete_pupil),
                "Are you sure you wish to delete pupil\n\n ${pupil.name}",
                positiveButtonText = "Yes",
                negativeButtonText = "No",
                positiveButtonClickListener = {
                    deletePupil()
                }
            )
        }
    }

    private fun deletePupil() {
        pupilViewModel.deletePupil(pupil.pupilId.toInt()).observe(viewLifecycleOwner) { response ->
            when(response.status) {
                Resource.Status.SUCCESS -> {
                    binding.loadingLinearLayout.visibility = View.GONE

                    Snackbar.make(binding.root, "Successfully deleted pupil", Snackbar.LENGTH_LONG).show()

                    val bundle = Bundle()
                    bundle.putBoolean(IS_DELETE_SUCCESSFUL, true)
                    requireActivity().supportFragmentManager.setFragmentResult(UPDATE_PUPIL_KEY, bundle)

                    requireActivity().supportFragmentManager.popBackStack()
                }
                Resource.Status.ERROR -> {
                    binding.loadingLinearLayout.visibility = View.GONE
                    Snackbar.make(binding.root, response.message.toString(), Snackbar.LENGTH_LONG).show()
                    Log.d(TAG, "Error Deleting Pupil: ${response.message}")
                }
                Resource.Status.LOADING -> {
                    binding.loadingLinearLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupFragmentResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(EDIT_PUPIL_KEY, this) { requestKey, bundle ->
            if (requestKey == EDIT_PUPIL_KEY) {
                val isUpdateSuccessful = bundle.getBoolean(IS_UPDATE_SUCCESSFUL, false)

                if (isUpdateSuccessful) {
                    handleResult()
                }
            }
        }
        requireActivity().supportFragmentManager.clearFragmentResult(EDIT_PUPIL_KEY)
    }

    private fun handleResult() {
        val bundle = Bundle()
        bundle.putBoolean(IS_UPDATE_SUCCESSFUL, true)
        requireActivity().supportFragmentManager.setFragmentResult(UPDATE_PUPIL_KEY, bundle)

        val fm = requireActivity().supportFragmentManager
        fm.popBackStack()
    }

    companion object {
        private val TAG = PupilDetailFragment::class.simpleName
    }
}