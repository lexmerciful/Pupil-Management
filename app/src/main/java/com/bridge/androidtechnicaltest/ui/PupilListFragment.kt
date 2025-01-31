package com.bridge.androidtechnicaltest.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.adapter.PupilAdapter
import com.bridge.androidtechnicaltest.databinding.FragmentPupillistBinding
import com.bridge.androidtechnicaltest.db.Pupil
import com.bridge.androidtechnicaltest.db.PupilList
import com.bridge.androidtechnicaltest.util.Constants
import com.bridge.androidtechnicaltest.util.Constants.ADD_PUPIL_KEY
import com.bridge.androidtechnicaltest.util.Constants.UPDATE_PUPIL_KEY
import com.bridge.androidtechnicaltest.util.Constants.PUPIL
import com.bridge.androidtechnicaltest.util.Resource
import com.bridge.androidtechnicaltest.viewmodel.PupilViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PupilListFragment : Fragment() {

    private lateinit var binding: FragmentPupillistBinding
    private lateinit var pupilAdapter: PupilAdapter
    private val pupilViewModel by viewModels<PupilViewModel>()
    private var pupilList: MutableList<Pupil> = mutableListOf()

    companion object {
        private val TAG = PupilListFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPupillistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        setupProductRecyclerView()

        getPupils()

        setupCreateNewPupil()

        setupSwipeRefresh()

        setupFragmentResultListener()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.pupilListToolbar)

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.apply {
            setDisplayShowHomeEnabled(true)
            title = "NewGlobe Pupils Management"
        }
    }

    private fun setupProductRecyclerView() {
        pupilAdapter = PupilAdapter()
        binding.pupilListRecyclerView.apply {
            adapter = pupilAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        pupilAdapter.onItemClick = { pupil ->

            navigateFragment(pupil)

        }
    }

    private fun navigateFragment(pupil: Pupil) {
        val pupilDetailFragment = PupilDetailFragment()

        val bundle = bundleOf(PUPIL to Gson().toJson(pupil))
        pupilDetailFragment.arguments = bundle

        val fm = requireActivity().supportFragmentManager
        val tran = fm.beginTransaction().apply {
            hide(this@PupilListFragment)
            add(R.id.container, pupilDetailFragment)
            addToBackStack(null)
        }
        tran.commit()
    }

    private fun getPupils() {
        pupilViewModel.getAllPupil().observe(viewLifecycleOwner) { response ->
            when(response.status) {
                Resource.Status.SUCCESS -> {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.errorStateLinearLayout.visibility = View.GONE
                    val result = response.data
                    Log.d(TAG, "Pupils: $response")

                    if (result != null) {
                        pupilList = result.toMutableList()
                        pupilAdapter.differ.submitList(pupilList)
                    }
                    setupEmptyScreen(pupilList)
                }
                Resource.Status.ERROR -> {
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.errorStateLinearLayout.visibility = View.VISIBLE
                    Log.d(TAG, "Error Fetching Pupils: ${response.message}")
                }
                Resource.Status.LOADING -> {
                    binding.loadingProgressBar.isVisible = true
                    binding.errorStateLinearLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun setupCreateNewPupil() {
        binding.addPupilFab.setOnClickListener {

            val fm = requireActivity().supportFragmentManager
            val tran = fm.beginTransaction().apply {
                hide(this@PupilListFragment)
                add(R.id.container, AddPupilFragment())
                addToBackStack(null)
            }
            tran.commit()
        }
    }

    private fun setupSwipeRefresh() {
        binding.pupilListSwipeRefreshLayout.setOnRefreshListener {
            binding.pupilListSwipeRefreshLayout.isRefreshing = false
            getPupils()
        }
    }

    private fun setupEmptyScreen(pupilList: List<Pupil>) {
        if (pupilList.isEmpty()) {
            binding.errorStateLinearLayout.visibility = View.VISIBLE
        } else {
            binding.errorStateLinearLayout.visibility = View.GONE
        }
    }

    private fun setupFragmentResultListener() {
        requireActivity().supportFragmentManager.setFragmentResultListener(ADD_PUPIL_KEY, this) { requestKey, bundle ->
            if (requestKey == ADD_PUPIL_KEY) {
                val isUpdateSuccessful = bundle.getBoolean(Constants.IS_UPDATE_SUCCESSFUL, false)
                Log.d(TAG, "isUpdateSuccessful => $isUpdateSuccessful")

                if (isUpdateSuccessful) {
                    getPupils()
                }
            }
        }
        requireActivity().supportFragmentManager.clearFragmentResult(ADD_PUPIL_KEY)

         requireActivity().supportFragmentManager.setFragmentResultListener(UPDATE_PUPIL_KEY, this) { requestKey, bundle ->
            if (requestKey == UPDATE_PUPIL_KEY) {
                val isDeleteSuccessful = bundle.getBoolean(Constants.IS_DELETE_SUCCESSFUL, false)
                Log.d(TAG, "isDeleteSuccessful => $isDeleteSuccessful")

                val isUpdateSuccessful = bundle.getBoolean(Constants.IS_UPDATE_SUCCESSFUL, false)
                Log.d(TAG, "isUpdateSuccessful => $isUpdateSuccessful")

                if (isUpdateSuccessful || isDeleteSuccessful) {
                    getPupils()
                }
            }
        }
        requireActivity().supportFragmentManager.clearFragmentResult(UPDATE_PUPIL_KEY)
    }
}