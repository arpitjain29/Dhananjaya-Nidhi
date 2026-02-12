package com.dhananjayanidhi.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dhananjayanidhi.R
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityNomineeDetailsBinding
import com.dhananjayanidhi.models.CommonModel
import com.dhananjayanidhi.models.addressentry.AddressEntryModel
import com.dhananjayanidhi.models.membernomineedetails.MemberNomineeDetailsModel
import com.dhananjayanidhi.parameters.AddressEntryParams
import com.dhananjayanidhi.parameters.NomineeEntryParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseFragment
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.MemberFlowManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class NomineeDetailsActivity : BaseFragment() {
    private var nomineeDetailsBinding: ActivityNomineeDetailsBinding? = null
    private var addCustomerId: String? = null
    private var selectNomineeDob: String? = null
    private var isSubmitting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        nomineeDetailsBinding = ActivityNomineeDetailsBinding.inflate(inflater, container, false)
        return nomineeDetailsBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Validate step access
        if (!MemberFlowManager.canAccessStep(requireContext(), MemberFlowManager.FlowStep.NOMINEE)) {
            CommonFunction.showToastSingle(requireContext(), "Please complete previous steps first", 0)
            (activity as? CreateMemberActivity)?.navigateToPreviousStep()
            return
        }
        
        // Get customer ID from flow manager
        addCustomerId = MemberFlowManager.getCustomerId(requireContext())
        
        if (addCustomerId.isNullOrEmpty()) {
            CommonFunction.showToastSingle(requireContext(), "Customer ID not found. Please start from beginning.", 0)
            (activity as? CreateMemberActivity)?.navigateToPreviousStep()
            return
        }
        
        // Removed auto-navigation - allow user to view/edit completed steps when navigating back

        // Load nominee details from API
        loadNomineeDetails()

        nomineeDetailsBinding!!.etDobNomineeEntry.setOnClickListener {
            val c = Calendar.getInstance()
            val mYear: Int = c.get(Calendar.YEAR)
            val mMonth: Int = c.get(Calendar.MONTH)
            val mDay: Int = c.get(Calendar.DAY_OF_MONTH)

            // date picker dialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.DatePickerDialogTheme,
                { _, year, monthOfYear, dayOfMonth ->
                    val dateStr = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    nomineeDetailsBinding!!.etDobNomineeEntry.setText(dateStr)
                    selectNomineeDob = dateStr
                    // Clear error when date is selected
                    nomineeDetailsBinding!!.tilDobNomineeEntry.apply {
                        error = null
                        isErrorEnabled = false
                    }
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }
        
        // Add TextWatchers to clear errors when user types
        setupTextWatchers()

        nomineeDetailsBinding!!.btnSubmitNomineeEntry.setOnClickListener {
            // Validate all fields (local validation since no API exists)
            val nomineeEntryParams : NomineeEntryParams = NomineeEntryParams()
            nomineeEntryParams.nomineeName = nomineeDetailsBinding!!.etNameNomineeEntry.text.toString().trim()
            nomineeEntryParams.relation = nomineeDetailsBinding!!.etRelationNomineeEntry.text.toString().trim()
            nomineeEntryParams.dob = selectNomineeDob
            nomineeEntryParams.customerId = addCustomerId
           nomineeEntryParams.aadharNumber= nomineeDetailsBinding!!.etAddharNumberNomineeEntry.text.toString().trim()
//            val aadharCopy = nomineeDetailsBinding!!.etAddharCopyNomineeEntry.text.toString().trim()
            
            // Clear all previous errors
            nomineeDetailsBinding!!.tilNameNomineeEntry.apply {
                error = null
                isErrorEnabled = false
            }
            nomineeDetailsBinding!!.tilRelationNomineeEntry.apply {
                error = null
                isErrorEnabled = false
            }
            nomineeDetailsBinding!!.tilDobNomineeEntry.apply {
                error = null
                isErrorEnabled = false
            }
            nomineeDetailsBinding!!.tilAddharNumberNomineeEntry.apply {
                error = null
                isErrorEnabled = false
            }
//            nomineeDetailsBinding!!.tilAddharCopyNomineeEntry.apply {
//                error = null
//                isErrorEnabled = false
//            }
            
            var hasError = false
            
            if (TextUtils.isEmpty(nomineeEntryParams.nomineeName)) {
                nomineeDetailsBinding!!.tilNameNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Please enter nominee name"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(nomineeEntryParams.relation)) {
                nomineeDetailsBinding!!.tilRelationNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Please enter relation"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(nomineeEntryParams.dob)) {
                nomineeDetailsBinding!!.tilDobNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Please enter date of birth"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(nomineeEntryParams.aadharNumber)) {
                nomineeDetailsBinding!!.tilAddharNumberNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Please enter Aadhar number"
                }
                hasError = true
            }
            if (!TextUtils.isEmpty(nomineeEntryParams.aadharNumber) && nomineeEntryParams.aadharNumber?.length != 12) {
                nomineeDetailsBinding!!.tilAddharNumberNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Aadhar number must be 12 digits"
                }
                hasError = true
            }
//            if (TextUtils.isEmpty(aadharCopy)) {
//                nomineeDetailsBinding!!.tilAddharCopyNomineeEntry.apply {
//                    isErrorEnabled = true
//                    error = "Please enter Aadhar copy"
//                }
//                hasError = true
//            }
            
            if (!hasError) {
                isSubmitting = true
                nomineeDetailsBinding!!.btnSubmitNomineeEntry.isEnabled = false
                nomineeDetailsApi(nomineeEntryParams)
            }
        }
    }
    
    private fun setupTextWatchers() {
        fun createErrorClearingWatcher(til: com.google.android.material.textfield.TextInputLayout?) = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                til?.error = null
                til?.isErrorEnabled = false
            }
        }
        
        nomineeDetailsBinding?.etNameNomineeEntry?.addTextChangedListener(
            createErrorClearingWatcher(nomineeDetailsBinding?.tilNameNomineeEntry)
        )
        nomineeDetailsBinding?.etRelationNomineeEntry?.addTextChangedListener(
            createErrorClearingWatcher(nomineeDetailsBinding?.tilRelationNomineeEntry)
        )
        nomineeDetailsBinding?.etAddharNumberNomineeEntry?.addTextChangedListener(
            createErrorClearingWatcher(nomineeDetailsBinding?.tilAddharNumberNomineeEntry)
        )
//        nomineeDetailsBinding?.etAddharCopyNomineeEntry?.addTextChangedListener(
//            createErrorClearingWatcher(nomineeDetailsBinding?.tilAddharCopyNomineeEntry)
//        )
    }
    
    private fun loadNomineeDetails() {
        if (addCustomerId.isNullOrEmpty()) return
        
        if (isConnectingToInternet(requireContext())) {
            showProgressDialog()
            val call = ApiClient.buildService(activity).memberNomineeDetailsApi(addCustomerId!!)
            call?.enqueue(object : Callback<MemberNomineeDetailsModel?> {
                override fun onResponse(
                    call: Call<MemberNomineeDetailsModel?>,
                    response: Response<MemberNomineeDetailsModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val nomineeDetailsModel: MemberNomineeDetailsModel? = response.body()
                        if (nomineeDetailsModel != null && nomineeDetailsModel.status == true) {
                            val data = nomineeDetailsModel.data
                            data?.let { nomineeData ->
                                // Set name (combine first and last name)
                                val fullName = "${nomineeData.firstName ?: ""} ${nomineeData.lastName ?: ""}".trim()
                                nomineeDetailsBinding?.etNameNomineeEntry?.setText(fullName)
                                
                                // Set relation
                                nomineeData.relation?.let {
                                    nomineeDetailsBinding?.etRelationNomineeEntry?.setText(it)
                                }
                                
                                // Parse and set DOB
                                nomineeData.dob?.let { dobStr ->
                                    try {
                                        // Convert from "1993-12-31" to "31/12/1993"
                                        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                        val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                        val date = inputFormat.parse(dobStr)
                                        date?.let {
                                            val formattedDate = outputFormat.format(it)
                                            nomineeDetailsBinding?.etDobNomineeEntry?.setText(formattedDate)
                                            selectNomineeDob = formattedDate
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                
                                // Set Aadhar number
                                nomineeData.aadharNumber?.let {
                                    nomineeDetailsBinding?.etAddharNumberNomineeEntry?.setText(it)
                                }
                                
                                // Set Aadhar copy (if available)
//                                nomineeData.aadharNumber?.let {
//                                    nomineeDetailsBinding?.etAddharCopyNomineeEntry?.setText(it)
//                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<MemberNomineeDetailsModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    throwable.printStackTrace()
                }
            })
        }
    }
    private fun nomineeDetailsApi(nomineeEntryParams: NomineeEntryParams) {
        if (isConnectingToInternet(requireContext())) {
            showProgressDialog()
            val call1 = ApiClient.buildService(activity).nomineeDetailsApi(nomineeEntryParams)
            call1?.enqueue(object : Callback<CommonModel?> {
                override fun onResponse(
                    call: Call<CommonModel?>,
                    response: Response<CommonModel?>
                ) {
                    hideProgressDialog()
                    isSubmitting = false
                    nomineeDetailsBinding!!.btnSubmitNomineeEntry.isEnabled = true

                    if (response.isSuccessful) {
                        val addressEntryModel: CommonModel? = response.body()
                        if (addressEntryModel != null) {
                            if (addressEntryModel.success == true) {
                                // Mark step as completed
                                MemberFlowManager.markStepCompleted(requireContext(),
                                    MemberFlowManager.FlowStep.NOMINEE)

                                // Update stepper in parent activity
                                (activity as? CreateMemberActivity)?.updateStepper()

                                // Show success message
                                CommonFunction.showToastSingle(requireContext(),
                                    "Nominee details saved successfully", 0)

                                // Navigate to next step
                                navigateToNextStep()
                            } else {
                                val errorMsg = addressEntryModel.message ?: "Failed to save address details"
                                CommonFunction.showToastSingle(requireContext(), errorMsg, 0)
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                val errorJson = JSONObject(errorBody)
                                val errorArray = errorJson.getJSONArray("error")
                                val errorMessage = errorArray.getJSONObject(0).getString("message")
                                CommonFunction.showToastSingle(requireContext(), errorMessage, 0)
                                AppController.instance?.sessionManager?.logoutUser()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                AppController.instance?.sessionManager?.logoutUser()
                                CommonFunction.showToastSingle(
                                    requireContext(),
                                    "An error occurred. Please try again.",
                                    0
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<CommonModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    isSubmitting = false
                    nomineeDetailsBinding!!.btnSubmitNomineeEntry.isEnabled = true

                    throwable.printStackTrace()
                    CommonFunction.showToastSingle(
                        requireContext(),
                        "Network error. Please check your connection and try again.",
                        0
                    )
                }
            })
        } else {
            CommonFunction.showToastSingle(
                requireContext(),
                resources.getString(R.string.net_connection), 0
            )
        }
    }

    private fun navigateToNextStep() {
        (activity as? CreateMemberActivity)?.navigateToNextStep()
    }
}