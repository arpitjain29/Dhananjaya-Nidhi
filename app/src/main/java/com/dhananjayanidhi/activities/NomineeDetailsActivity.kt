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
import com.dhananjayanidhi.models.membernomineedetails.MemberNomineeDetailsModel
import com.dhananjayanidhi.utils.BaseFragment
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.MemberFlowManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class NomineeDetailsActivity : BaseFragment() {
    private var nomineeDetailsBinding: ActivityNomineeDetailsBinding? = null
    private var addCustomerId: String? = null
    private var selectNomineeDob: String? = null
    
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
            val name = nomineeDetailsBinding!!.etNameNomineeEntry.text.toString().trim()
            val relation = nomineeDetailsBinding!!.etRelationNomineeEntry.text.toString().trim()
            val dob = selectNomineeDob
            val aadharNumber = nomineeDetailsBinding!!.etAddharNumberNomineeEntry.text.toString().trim()
            val aadharCopy = nomineeDetailsBinding!!.etAddharCopyNomineeEntry.text.toString().trim()
            
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
            nomineeDetailsBinding!!.tilAddharCopyNomineeEntry.apply {
                error = null
                isErrorEnabled = false
            }
            
            var hasError = false
            
            if (TextUtils.isEmpty(name)) {
                nomineeDetailsBinding!!.tilNameNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Please enter nominee name"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(relation)) {
                nomineeDetailsBinding!!.tilRelationNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Please enter relation"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(dob)) {
                nomineeDetailsBinding!!.tilDobNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Please enter date of birth"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(aadharNumber)) {
                nomineeDetailsBinding!!.tilAddharNumberNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Please enter Aadhar number"
                }
                hasError = true
            }
            if (!TextUtils.isEmpty(aadharNumber) && aadharNumber.length != 12) {
                nomineeDetailsBinding!!.tilAddharNumberNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Aadhar number must be 12 digits"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(aadharCopy)) {
                nomineeDetailsBinding!!.tilAddharCopyNomineeEntry.apply {
                    isErrorEnabled = true
                    error = "Please enter Aadhar copy"
                }
                hasError = true
            }
            
            if (!hasError) {
                // All validations passed - mark step as completed and proceed
                MemberFlowManager.markStepCompleted(requireContext(), MemberFlowManager.FlowStep.NOMINEE)
                
                // Update stepper in parent activity
                (activity as? CreateMemberActivity)?.updateStepper()
                
                CommonFunction.showToastSingle(requireContext(), "Nominee details saved successfully", 0)
                navigateToNextStep()
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
        nomineeDetailsBinding?.etAddharCopyNomineeEntry?.addTextChangedListener(
            createErrorClearingWatcher(nomineeDetailsBinding?.tilAddharCopyNomineeEntry)
        )
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
                                nomineeData.aadharNumber?.let {
                                    nomineeDetailsBinding?.etAddharCopyNomineeEntry?.setText(it)
                                }
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

    private fun navigateToNextStep() {
        (activity as? CreateMemberActivity)?.navigateToNextStep()
    }
}