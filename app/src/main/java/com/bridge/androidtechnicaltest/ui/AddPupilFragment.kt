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
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.afollestad.vvalidator.form
import com.bridge.androidtechnicaltest.databinding.FragmentAddPupilBinding
import com.bridge.androidtechnicaltest.db.Pupil
import com.bridge.androidtechnicaltest.util.Constants
import com.bridge.androidtechnicaltest.util.Constants.ADD_PUPIL_KEY
import com.bridge.androidtechnicaltest.util.Constants.EDIT_PUPIL_KEY
import com.bridge.androidtechnicaltest.util.Constants.IS_EDIT
import com.bridge.androidtechnicaltest.util.Constants.IS_UPDATE_SUCCESSFUL
import com.bridge.androidtechnicaltest.util.Constants.showAlertDialog
import com.bridge.androidtechnicaltest.util.Resource
import com.bridge.androidtechnicaltest.util.isNetworkAvailable
import com.bridge.androidtechnicaltest.viewmodel.PupilViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AddPupilFragment : Fragment() {

    private lateinit var binding: FragmentAddPupilBinding
    private val pupilViewModel by viewModels<PupilViewModel>()
    private var isEdit = false
    private lateinit var pupil: Pupil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddPupilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setupToolbar()

        populateData()

        setupForm()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.addPupilToolbar)

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = if (isEdit) "Edit Pupil Details" else "Add New Pupil"
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
        isEdit = arguments?.getBoolean(IS_EDIT, false) == true

        if (arguments?.getString(Constants.PUPIL) != null) {
            pupil = Gson().fromJson(arguments?.getString(Constants.PUPIL), Pupil::class.java)
        }
    }

    private fun populateData() {
        if (isEdit) {
            binding.nameEditText.setText(pupil.name)

            binding.countryEditText.setText(pupil.country)
        }
    }

    private fun setupForm() {
        form {
            useRealTimeValidation(disableSubmit = true)

            input(binding.nameEditText) {
                isNotEmpty().description("Name cannot be empty")
                length().atLeast(2).description("Enter a valid name")
                length().atMost(100).description("Name too long")
            }

            input(binding.countryEditText) {
                isNotEmpty().description("Country cannot be empty")
                length().atLeast(2).description("Enter a valid country")
                length().atMost(100).description("Country name too long")
            }

            submitWith(binding.proceedButton) {
                val name = binding.nameEditText.text.toString()
                val country = binding.countryEditText.text.toString()
                val image =  "https://www.heritagechristian.net/uploaded/Heritage_Photos/Academics/Middle/Middle02.jpg"
                val log = 150.02
                val lat = 50.01

                val pupil = Pupil(0, name, country, image, lat, log)
                Log.d(TAG, "Pupil Json: $pupil")

                if (!isEdit) {
                    //addNewPupil(pupil)
                    onAddPupilClicked(pupil)
                } else {
                    editPupil()
                }

            }
        }
    }


    private fun onAddPupilClicked(pupil: Pupil) {
     val workId = pupilViewModel.createNewPupil(pupil)

     // Observe WorkManager status
    observeWorkStatus(workId)
    }

    private fun observeWorkStatus(workId: UUID) {
        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(workId)
            .observe(viewLifecycleOwner) { workInfo ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {

                            binding.loadingLinearLayout.visibility = View.GONE
                            Snackbar.make(binding.root, "Successfully created pupil ✅", Snackbar.LENGTH_LONG).show()
                            handleAddResult()

                        }

                        WorkInfo.State.FAILED -> {
                            binding.loadingLinearLayout.visibility = View.GONE

                            val errorMessage = "Error Occurred while creating pupil! Please try again."
                            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
                            Log.d(TAG, "Error Creating Pupil: ${workInfo.outputData}")
                        }

                        WorkInfo.State.ENQUEUED -> {

                            val networkAvailable = isNetworkAvailable(requireContext())
                            if (!networkAvailable) {
                                showAlertDialog(
                                    requireContext(),
                                    "Create Pupil Scheduled",
                                    "No Internet Connection🚧\n\nDon't Worry, Pupil will be automatically created when network is restored"
                                )
                            }
                        }

                        WorkInfo.State.RUNNING -> {
                            binding.loadingLinearLayout.visibility = View.VISIBLE
                        }

                        else -> {
                            // Handle other states if necessary
                        }
                    }
                }
            }
    }


    private fun addNewPupil(pupil: Pupil) {
        val jsonObject = JsonObject()
        jsonObject.apply {
            addProperty("name", pupil.name)
            addProperty("country", pupil.country)
            addProperty("image", pupil.image)
            addProperty("latitude", pupil.latitude)
            addProperty("longitude", pupil.longitude)
        }
        Log.d(TAG, "Pupil Json: $jsonObject")

        pupilViewModel.createNewPupilOld(jsonObject).observe(viewLifecycleOwner) { response ->
            when(response.status) {
                Resource.Status.SUCCESS -> {
                    binding.loadingLinearLayout.visibility = View.GONE
                    Snackbar.make(binding.root, "Successfully created ${response.data?.name}", Snackbar.LENGTH_LONG).show()

                    handleAddResult()
                }
                Resource.Status.ERROR -> {
                    binding.loadingLinearLayout.visibility = View.GONE
                    Snackbar.make(binding.root, response.message.toString(), Snackbar.LENGTH_LONG).show()
                    Log.d(TAG, "Error Creating Pupil: ${response.message}")
                }
                Resource.Status.LOADING -> {
                    binding.loadingLinearLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun editPupil() {
        val name = binding.nameEditText.text.toString()
        val country = binding.countryEditText.text.toString()
        val jsonObject = JsonObject()
        jsonObject.apply {
            addProperty("name", name)
            addProperty("country", country)
            addProperty("pupilId", pupil.pupilId)
            addProperty("image", pupil.image)
            addProperty("latitude", pupil.latitude)
            addProperty("longitude", pupil.longitude)
        }
        Log.d(TAG, "Edit Pupil Json: $jsonObject")

        pupilViewModel.editPupil(pupil.pupilId.toInt(), jsonObject).observe(viewLifecycleOwner) { response ->
            when(response.status) {
                Resource.Status.SUCCESS -> {
                    binding.loadingLinearLayout.visibility = View.GONE

                    Snackbar.make(binding.root, "Successfully updated ${response.data?.name}", Snackbar.LENGTH_LONG).show()
                    handleEditResult()
                }
                Resource.Status.ERROR -> {
                    binding.loadingLinearLayout.visibility = View.GONE
                    Snackbar.make(binding.root, response.message.toString(), Snackbar.LENGTH_LONG).show()
                    Log.d(TAG, "Error Updating Pupil: ${response.message}")
                }
                Resource.Status.LOADING -> {
                    binding.loadingLinearLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun clearEditTexts() {
        binding.nameEditText.setText("")
        binding.countryEditText.setText("")
    }

    private fun handleAddResult() {
        val bundle = Bundle()
        bundle.putBoolean(IS_UPDATE_SUCCESSFUL, true)
        requireActivity().supportFragmentManager.setFragmentResult(ADD_PUPIL_KEY, bundle)

        val fm = requireActivity().supportFragmentManager
        fm.popBackStack()
    }

    private fun handleEditResult() {
        val bundle = Bundle()
        bundle.putBoolean(IS_UPDATE_SUCCESSFUL, true)
        requireActivity().supportFragmentManager.setFragmentResult(EDIT_PUPIL_KEY, bundle)

        val fm = requireActivity().supportFragmentManager
        fm.popBackStack()
    }

    companion object {
        private val TAG = AddPupilFragment::class.simpleName

        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }
}