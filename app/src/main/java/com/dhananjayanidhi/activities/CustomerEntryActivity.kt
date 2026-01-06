package com.dhananjayanidhi.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dhananjayanidhi.R
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityCustomerEntryBinding
import com.dhananjayanidhi.models.customeradd.CustomerAddModel
import com.dhananjayanidhi.parameters.CustomerAddParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseFragment
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.MemberFlowManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class CustomerEntryActivity : BaseFragment() {
    private var customerEntryBinding: ActivityCustomerEntryBinding? = null
    private var selectDateOfBirth: String? = null
    private var isSubmitting = false // Prevent multiple API calls

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        customerEntryBinding = ActivityCustomerEntryBinding.inflate(inflater, container, false)
        return customerEntryBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Load customer data if available (from pending members)
        val customerData = arguments?.getSerializable("customer_data") as? com.dhananjayanidhi.models.memberdraft.DatumMemberDraftModel
        if (customerData != null) {
            populateCustomerData(customerData)
        } else {
            // Load saved customer data when navigating back
            loadSavedCustomerData()
        }

        customerEntryBinding?.etDobCustomerEntry?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.DatePickerDialogTheme,
                { _, year, monthOfYear, dayOfMonth ->
                    val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    customerEntryBinding?.etDobCustomerEntry?.setText(dat)
                    selectDateOfBirth = dat
                    // Clear error when date is selected
                    customerEntryBinding?.tilDobCustomerEntry?.error = null
                    customerEntryBinding?.tilDobCustomerEntry?.isErrorEnabled = false
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        
        // Setup gender dropdown
        setupGenderDropdown()
        
        // Add TextWatchers to clear errors when user types
        setupTextWatchers()

        customerEntryBinding!!.btnSubmitDdsEntry.setOnClickListener {
            // Prevent multiple clicks
            if (isSubmitting) return@setOnClickListener
            
            val customerAddParams = CustomerAddParams()
            if (MemberFlowManager.getCustomerId(requireContext())?.isNotEmpty() == true)
                customerAddParams.customerId = MemberFlowManager.getCustomerId(requireContext())
            customerAddParams.firstName =
                customerEntryBinding?.etFirstNameCustomerEntry?.text.toString().trim()
            customerAddParams.lastName =
                customerEntryBinding?.etLastNameCustomerEntry?.text.toString().trim()
            customerAddParams.phoneNumber =
                customerEntryBinding?.etMobileNumberDdsEntry?.text.toString().trim()
            customerAddParams.dob = selectDateOfBirth
            customerAddParams.gender =
                customerEntryBinding?.etGenderCustomerEntry?.text.toString().trim()
            customerAddParams.fatherName =
                customerEntryBinding?.etFatherNameCustomerEntry?.text.toString().trim()
            customerAddParams.motherName =
                customerEntryBinding?.etMotherNameCustomerEntry?.text.toString().trim()
            customerAddParams.annualIncome =
                customerEntryBinding?.etAnnualIncomeEntry?.text.toString().trim()
            customerAddParams.occupation =
                customerEntryBinding?.etOccupationCustomerEntry?.text.toString().trim()
            customerAddParams.caste =
                customerEntryBinding?.etCasteCustomerEntry?.text.toString().trim()

            // Clear all previous errors
            clearAllErrors()
            
            var hasError = false
            
            if (TextUtils.isEmpty(customerAddParams.firstName)) {
                customerEntryBinding?.tilFirstNameCustomerEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_first_name)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(customerAddParams.lastName)) {
                customerEntryBinding?.tilLastNameCustomerEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_last_name)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(customerAddParams.phoneNumber) ||
                customerAddParams.phoneNumber!!.length != 10
            ) {
                customerEntryBinding?.tilMobileNumberDdsEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_mobile_number)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(customerAddParams.dob)) {
                customerEntryBinding?.tilDobCustomerEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_dob)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(customerAddParams.gender)) {
                customerEntryBinding?.tilGenderCustomerEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_gender)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(customerAddParams.fatherName)) {
                customerEntryBinding?.tilFatherNameCustomerEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_father_name)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(customerAddParams.motherName)) {
                customerEntryBinding?.tilMotherNameCustomerEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_mother_name)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(customerAddParams.annualIncome)) {
                customerEntryBinding?.tilAnnualIncomeEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_annual_income)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(customerAddParams.occupation)) {
                customerEntryBinding?.tilOccupationCustomerEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_occupation)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(customerAddParams.caste)) {
                customerEntryBinding?.tilCasteCustomerEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_your_casts)
                }
                hasError = true
            }
            
            if (!hasError) {
                isSubmitting = true
                customerEntryBinding!!.btnSubmitDdsEntry.isEnabled = false
                customerAddApi(customerAddParams)
            }
        }
    }

    private fun setupGenderDropdown() {
        val genderOptions = arrayOf("Male", "Female", "Other")
        
        // Create a dialog to show gender options when field or icon is clicked
        val showGenderDialog: () -> Unit = {
            val builder = android.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Select Gender")
            builder.setItems(genderOptions) { _, which ->
                val selectedGender = genderOptions[which]
                customerEntryBinding?.etGenderCustomerEntry?.setText(selectedGender)
                // Clear error when gender is selected
                customerEntryBinding?.tilGenderCustomerEntry?.error = null
                customerEntryBinding?.tilGenderCustomerEntry?.isErrorEnabled = false
            }
            builder.show()
        }
        
        // Click listener for the text field
        customerEntryBinding?.etGenderCustomerEntry?.setOnClickListener {
            showGenderDialog()
        }
        
        // Click listener for the dropdown arrow icon
        customerEntryBinding?.tilGenderCustomerEntry?.setEndIconOnClickListener {
            showGenderDialog()
        }
    }
    
    private fun clearAllErrors() {
        customerEntryBinding?.tilFirstNameCustomerEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        customerEntryBinding?.tilLastNameCustomerEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        customerEntryBinding?.tilMobileNumberDdsEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        customerEntryBinding?.tilDobCustomerEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        customerEntryBinding?.tilGenderCustomerEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        customerEntryBinding?.tilFatherNameCustomerEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        customerEntryBinding?.tilMotherNameCustomerEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        customerEntryBinding?.tilAnnualIncomeEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        customerEntryBinding?.tilOccupationCustomerEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        customerEntryBinding?.tilCasteCustomerEntry?.apply {
            error = null
            isErrorEnabled = false
        }
    }
    
    private fun setupTextWatchers() {
        // Create a simple TextWatcher that clears error for the associated TextInputLayout
        fun createErrorClearingWatcher(til: com.google.android.material.textfield.TextInputLayout?) = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                til?.error = null
                til?.isErrorEnabled = false
            }
        }
        
        customerEntryBinding?.etFirstNameCustomerEntry?.addTextChangedListener(
            createErrorClearingWatcher(customerEntryBinding?.tilFirstNameCustomerEntry)
        )
        customerEntryBinding?.etLastNameCustomerEntry?.addTextChangedListener(
            createErrorClearingWatcher(customerEntryBinding?.tilLastNameCustomerEntry)
        )
        customerEntryBinding?.etMobileNumberDdsEntry?.addTextChangedListener(
            createErrorClearingWatcher(customerEntryBinding?.tilMobileNumberDdsEntry)
        )
        // Gender field doesn't need TextWatcher as it's a dropdown
        customerEntryBinding?.etFatherNameCustomerEntry?.addTextChangedListener(
            createErrorClearingWatcher(customerEntryBinding?.tilFatherNameCustomerEntry)
        )
        customerEntryBinding?.etMotherNameCustomerEntry?.addTextChangedListener(
            createErrorClearingWatcher(customerEntryBinding?.tilMotherNameCustomerEntry)
        )
        customerEntryBinding?.etAnnualIncomeEntry?.addTextChangedListener(
            createErrorClearingWatcher(customerEntryBinding?.tilAnnualIncomeEntry)
        )
        customerEntryBinding?.etOccupationCustomerEntry?.addTextChangedListener(
            createErrorClearingWatcher(customerEntryBinding?.tilOccupationCustomerEntry)
        )
        customerEntryBinding?.etCasteCustomerEntry?.addTextChangedListener(
            createErrorClearingWatcher(customerEntryBinding?.tilCasteCustomerEntry)
        )
    }

    private fun populateCustomerData(customerData: com.dhananjayanidhi.models.memberdraft.DatumMemberDraftModel) {
        customerEntryBinding?.etFirstNameCustomerEntry?.setText(customerData.firstName ?: "")
        customerEntryBinding?.etLastNameCustomerEntry?.setText(customerData.lastName ?: "")
        customerEntryBinding?.etMobileNumberDdsEntry?.setText(customerData.mobileNumber ?: "")
        
        // Parse and set DOB
        customerData.dob?.let { dobStr ->
            try {
                // Convert from "1993-12-31" to "31/12/1993"
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                val date = inputFormat.parse(dobStr)
                date?.let {
                    val formattedDate = outputFormat.format(it)
                    customerEntryBinding?.etDobCustomerEntry?.setText(formattedDate)
                    selectDateOfBirth = formattedDate
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        customerEntryBinding?.etGenderCustomerEntry?.setText(customerData.gender ?: "")
        customerEntryBinding?.etFatherNameCustomerEntry?.setText(customerData.fatherName ?: "")
        customerEntryBinding?.etMotherNameCustomerEntry?.setText(customerData.motherName ?: "")
        customerEntryBinding?.etAnnualIncomeEntry?.setText(customerData.annualIncome ?: "")
        customerEntryBinding?.etOccupationCustomerEntry?.setText(customerData.occupation ?: "")
        customerEntryBinding?.etCasteCustomerEntry?.setText(customerData.caste ?: "")
        
        // Set customer ID in flow manager if not already set
        customerData.id?.let { customerId ->
            if (MemberFlowManager.getCustomerId(requireContext()).isNullOrEmpty()) {
                MemberFlowManager.setCustomerId(requireContext(), customerId.toString())
            }
        }
    }

    private fun loadSavedCustomerData() {
        // Only load if step is completed (meaning data was saved before)
        if (MemberFlowManager.isStepCompleted(requireContext(), MemberFlowManager.FlowStep.CUSTOMER)) {
            val savedData = MemberFlowManager.getSavedCustomerData(requireContext())
            
            savedData["first_name"]?.let {
                customerEntryBinding?.etFirstNameCustomerEntry?.setText(it)
            }
            savedData["last_name"]?.let {
                customerEntryBinding?.etLastNameCustomerEntry?.setText(it)
            }
            savedData["phone_number"]?.let {
                customerEntryBinding?.etMobileNumberDdsEntry?.setText(it)
            }
            savedData["dob"]?.let {
                customerEntryBinding?.etDobCustomerEntry?.setText(it)
                selectDateOfBirth = it
            }
            savedData["gender"]?.let {
                customerEntryBinding?.etGenderCustomerEntry?.setText(it)
            }
            savedData["father_name"]?.let {
                customerEntryBinding?.etFatherNameCustomerEntry?.setText(it)
            }
            savedData["mother_name"]?.let {
                customerEntryBinding?.etMotherNameCustomerEntry?.setText(it)
            }
            savedData["annual_income"]?.let {
                customerEntryBinding?.etAnnualIncomeEntry?.setText(it)
            }
            savedData["occupation"]?.let {
                customerEntryBinding?.etOccupationCustomerEntry?.setText(it)
            }
            savedData["caste"]?.let {
                customerEntryBinding?.etCasteCustomerEntry?.setText(it)
            }
        }
    }

    private fun navigateToNextStep() {
        (activity as? CreateMemberActivity)?.navigateToNextStep()
    }

    private fun customerAddApi(customerAddParams: CustomerAddParams) {
        if (isConnectingToInternet(requireContext())) {
            showProgressDialog()
            val call1 = ApiClient.buildService(activity).addCustomerApi(customerAddParams)
            call1?.enqueue(object : Callback<CustomerAddModel?> {
                override fun onResponse(
                    call: Call<CustomerAddModel?>,
                    response: Response<CustomerAddModel?>
                ) {
                    hideProgressDialog()
                    isSubmitting = false
                    customerEntryBinding!!.btnSubmitDdsEntry.isEnabled = true
                    
                    if (response.isSuccessful) {
                        val customerAddModel: CustomerAddModel? = response.body()
                        if (customerAddModel != null) {
                            if (customerAddModel.success == true) {
                                // CRITICAL: Only proceed to next step if API returns success=true
                                // Store customer ID for use in subsequent steps
                                val customerId = customerAddModel.data?.id
                                if (!customerId.isNullOrEmpty()) {
                                    // Persist customer ID and step completion state
                                    MemberFlowManager.setCustomerId(requireContext(), customerId)
                                    MemberFlowManager.markStepCompleted(requireContext(), MemberFlowManager.FlowStep.CUSTOMER)
                                    
                                    // Save customer data for later retrieval when navigating back
                                    MemberFlowManager.saveCustomerData(
                                        requireContext(),
                                        customerAddParams.firstName,
                                        customerAddParams.lastName,
                                        customerAddParams.phoneNumber,
                                        customerAddParams.dob,
                                        customerAddParams.gender,
                                        customerAddParams.fatherName,
                                        customerAddParams.motherName,
                                        customerAddParams.annualIncome,
                                        customerAddParams.occupation,
                                        customerAddParams.caste
                                    )
                                    
                                    // Update stepper in parent activity
                                    (activity as? CreateMemberActivity)?.updateStepper()
                                    
                                    // Show success message
                                    CommonFunction.showToastSingle(requireContext(), 
                                        customerAddModel.message ?: "Customer details saved successfully", 0)
                                    
                                    // Navigate to next step only after successful API response
                                    navigateToNextStep()
                                } else {
                                    CommonFunction.showToastSingle(requireContext(), 
                                        "Error: Customer ID not received", 0)
                                }
                            } else {
                                // API returned success=false
                                val errorMsg = customerAddModel.message ?: "Failed to save customer details"
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

                override fun onFailure(call: Call<CustomerAddModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    isSubmitting = false
                    customerEntryBinding!!.btnSubmitDdsEntry.isEnabled = true
                    
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
}