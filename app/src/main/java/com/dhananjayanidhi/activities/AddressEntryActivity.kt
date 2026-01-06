package com.dhananjayanidhi.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dhananjayanidhi.R
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityAddressEntryBinding
import com.dhananjayanidhi.models.addressentry.AddressEntryModel
import com.dhananjayanidhi.models.memberaddressinfo.MemberAddressInfoModel
import com.dhananjayanidhi.parameters.AddressEntryParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseFragment
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.MemberFlowManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressEntryActivity : BaseFragment() {
    private var addressEntryActivity: ActivityAddressEntryBinding? = null
    private var addCustomerId: String? = null
    private var isSubmitting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addressEntryActivity = ActivityAddressEntryBinding.inflate(inflater, container, false)
        return addressEntryActivity?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Validate step access
        if (!MemberFlowManager.canAccessStep(requireContext(), MemberFlowManager.FlowStep.ADDRESS)) {
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

        // Add TextWatchers to clear errors when user types
        setupTextWatchers()
        
        // Setup copy to address functionality
        setupCopyToAddress()
        
        // Load address data from API (after setting up listeners)
        loadAddressInfo()

        addressEntryActivity!!.btnSubmitAddressEntry.setOnClickListener {
            // Prevent multiple clicks
            if (isSubmitting) return@setOnClickListener
            
            val addressEntryParams = AddressEntryParams()
            addressEntryParams.customerId = addCustomerId
            addressEntryParams.presentHouseNo =
                addressEntryActivity?.etHouseNoAddressEntry?.text.toString().trim()
            addressEntryParams.presentAddress =
                addressEntryActivity?.etAddressEntry?.text.toString().trim()
            addressEntryParams.presentLandmark =
                addressEntryActivity?.etLandmarkAddressEntry?.text.toString().trim()
            addressEntryParams.presentCity =
                addressEntryActivity?.etCityAddressEntry?.text.toString().trim()
            addressEntryParams.presentPincode =
                addressEntryActivity?.etPincodeAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentHouseNo =
                addressEntryActivity?.etHouseNoPermanentAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentAddress =
                addressEntryActivity?.etPermanentAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentLandmark =
                addressEntryActivity?.etLandmarkPermanentAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentCity =
                addressEntryActivity?.etCityPermanentAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentPincode =
                addressEntryActivity?.etPincodePermanentAddressEntry?.text.toString().trim()

            // Clear all previous errors
            addressEntryActivity?.tilHouseNoAddressEntry?.apply {
                error = null
                isErrorEnabled = false
            }
            addressEntryActivity?.tilAddressEntry?.apply {
                error = null
                isErrorEnabled = false
            }
            addressEntryActivity?.tilLandmarkAddressEntry?.apply {
                error = null
                isErrorEnabled = false
            }
            addressEntryActivity?.tilCityAddressEntry?.apply {
                error = null
                isErrorEnabled = false
            }
            addressEntryActivity?.tilPincodeAddressEntry?.apply {
                error = null
                isErrorEnabled = false
            }
            
            var hasError = false
            
            if (TextUtils.isEmpty(addressEntryParams.presentHouseNo)) {
                addressEntryActivity?.tilHouseNoAddressEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_house_no)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(addressEntryParams.presentAddress)) {
                addressEntryActivity?.tilAddressEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_address)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(addressEntryParams.presentLandmark)) {
                addressEntryActivity?.tilLandmarkAddressEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_landmark)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(addressEntryParams.presentCity)) {
                addressEntryActivity?.tilCityAddressEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_city)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(addressEntryParams.presentPincode)) {
                addressEntryActivity?.tilPincodeAddressEntry?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_pincode)
                }
                hasError = true
            } else if (addressEntryParams.presentPincode!!.length != 6 || !addressEntryParams.presentPincode!!.all { it.isDigit() }) {
                addressEntryActivity?.tilPincodeAddressEntry?.apply {
                    isErrorEnabled = true
                    error = "Pincode must be exactly 6 digits"
                }
                hasError = true
            }
            
            if (!hasError) {
                isSubmitting = true
                addressEntryActivity!!.btnSubmitAddressEntry.isEnabled = false
                addressEntryApi(addressEntryParams)
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
        
        addressEntryActivity?.etHouseNoAddressEntry?.addTextChangedListener(
            createErrorClearingWatcher(addressEntryActivity?.tilHouseNoAddressEntry)
        )
        addressEntryActivity?.etAddressEntry?.addTextChangedListener(
            createErrorClearingWatcher(addressEntryActivity?.tilAddressEntry)
        )
        addressEntryActivity?.etLandmarkAddressEntry?.addTextChangedListener(
            createErrorClearingWatcher(addressEntryActivity?.tilLandmarkAddressEntry)
        )
        addressEntryActivity?.etCityAddressEntry?.addTextChangedListener(
            createErrorClearingWatcher(addressEntryActivity?.tilCityAddressEntry)
        )
        addressEntryActivity?.etPincodeAddressEntry?.addTextChangedListener(
            createErrorClearingWatcher(addressEntryActivity?.tilPincodeAddressEntry)
        )
        addressEntryActivity?.etPincodePermanentAddressEntry?.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    // Clear error if user is typing (permanent pincode is optional but should be valid if provided)
                    addressEntryActivity?.tilPincodeAddressEntry?.error = null
                    addressEntryActivity?.tilPincodeAddressEntry?.isErrorEnabled = false
                }
            }
        )
    }
    
    private fun setupCopyToAddress() {
        // Handle checkbox state change - copy current values when checked, clear when unchecked
        addressEntryActivity?.cbCopyToAddressEntry?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Copy current values when checkbox is checked
                copyPresentToPermanent()
            } else {
                // Clear permanent address fields when checkbox is unchecked
                addressEntryActivity?.etHouseNoPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
                addressEntryActivity?.etPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
                addressEntryActivity?.etLandmarkPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
                addressEntryActivity?.etCityPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
                addressEntryActivity?.etPincodePermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
            }
        }
        
        // Copy to permanent address in real-time when checkbox is checked
        addressEntryActivity?.etHouseNoAddressEntry?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (addressEntryActivity?.cbCopyToAddressEntry?.isChecked == true) {
                    val text = s?.toString() ?: ""
                    addressEntryActivity?.etHouseNoPermanentAddressEntry?.setText(text)
                    addressEntryActivity?.etHouseNoPermanentAddressEntry?.setSelection(text.length)
                }
            }
        })
        
        addressEntryActivity?.etAddressEntry?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (addressEntryActivity?.cbCopyToAddressEntry?.isChecked == true) {
                    val text = s?.toString() ?: ""
                    addressEntryActivity?.etPermanentAddressEntry?.setText(text)
                    addressEntryActivity?.etPermanentAddressEntry?.setSelection(text.length)
                }
            }
        })
        
        addressEntryActivity?.etLandmarkAddressEntry?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (addressEntryActivity?.cbCopyToAddressEntry?.isChecked == true) {
                    val text = s?.toString() ?: ""
                    addressEntryActivity?.etLandmarkPermanentAddressEntry?.setText(text)
                    addressEntryActivity?.etLandmarkPermanentAddressEntry?.setSelection(text.length)
                }
            }
        })
        
        addressEntryActivity?.etCityAddressEntry?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (addressEntryActivity?.cbCopyToAddressEntry?.isChecked == true) {
                    val text = s?.toString() ?: ""
                    addressEntryActivity?.etCityPermanentAddressEntry?.setText(text)
                    addressEntryActivity?.etCityPermanentAddressEntry?.setSelection(text.length)
                }
            }
        })
        
        addressEntryActivity?.etPincodeAddressEntry?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (addressEntryActivity?.cbCopyToAddressEntry?.isChecked == true) {
                    val text = s?.toString() ?: ""
                    addressEntryActivity?.etPincodePermanentAddressEntry?.setText(text)
                    addressEntryActivity?.etPincodePermanentAddressEntry?.setSelection(text.length)
                }
            }
        })
    }
    
    private fun copyPresentToPermanent() {
        addressEntryActivity?.etHouseNoPermanentAddressEntry?.text =
            Editable.Factory.getInstance().newEditable(
                addressEntryActivity?.etHouseNoAddressEntry?.text.toString().trim()
            )
        addressEntryActivity?.etPermanentAddressEntry?.text = Editable.Factory.getInstance()
            .newEditable(addressEntryActivity?.etAddressEntry?.text.toString().trim())
        addressEntryActivity?.etLandmarkPermanentAddressEntry?.text =
            Editable.Factory.getInstance().newEditable(
                addressEntryActivity?.etLandmarkAddressEntry?.text.toString().trim()
            )
        addressEntryActivity?.etCityPermanentAddressEntry?.text =
            Editable.Factory.getInstance().newEditable(
                addressEntryActivity?.etCityAddressEntry?.text.toString().trim()
            )
        addressEntryActivity?.etPincodePermanentAddressEntry?.text =
            Editable.Factory.getInstance().newEditable(
                addressEntryActivity?.etPincodeAddressEntry?.text.toString().trim()
            )
    }

    private fun loadAddressInfo() {
        if (addCustomerId.isNullOrEmpty()) return
        
        if (isConnectingToInternet(requireContext())) {
            showProgressDialog()
            val call = ApiClient.buildService(activity).memberAddressInfoApi(addCustomerId!!)
            call?.enqueue(object : Callback<MemberAddressInfoModel?> {
                override fun onResponse(
                    call: Call<MemberAddressInfoModel?>,
                    response: Response<MemberAddressInfoModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val addressInfoModel: MemberAddressInfoModel? = response.body()
                        if (addressInfoModel != null && addressInfoModel.status == true) {
                            val data = addressInfoModel.data
                            data?.present?.let { present ->
                                addressEntryActivity?.etHouseNoAddressEntry?.setText(present.houseNo ?: "")
                                addressEntryActivity?.etAddressEntry?.setText(present.address ?: "")
                                addressEntryActivity?.etLandmarkAddressEntry?.setText(present.landmark ?: "")
                                addressEntryActivity?.etCityAddressEntry?.setText(present.city ?: "")
                                addressEntryActivity?.etPincodeAddressEntry?.setText(present.pincode ?: "")
                            }
                            data?.parmanent?.let { permanent ->
                                addressEntryActivity?.etHouseNoPermanentAddressEntry?.setText(permanent.houseNo ?: "")
                                addressEntryActivity?.etPermanentAddressEntry?.setText(permanent.address ?: "")
                                addressEntryActivity?.etLandmarkPermanentAddressEntry?.setText(permanent.landmark ?: "")
                                addressEntryActivity?.etCityPermanentAddressEntry?.setText(permanent.city ?: "")
                                addressEntryActivity?.etPincodePermanentAddressEntry?.setText(permanent.pincode ?: "")
                            }
                            
                            // Check if both addresses are the same and check the checkbox
                            data?.present?.let { present ->
                                data?.parmanent?.let { permanent ->
                                    val areAddressesSame = 
                                        (present.houseNo?.trim() ?: "") == (permanent.houseNo?.trim() ?: "") &&
                                        (present.address?.trim() ?: "") == (permanent.address?.trim() ?: "") &&
                                        (present.landmark?.trim() ?: "") == (permanent.landmark?.trim() ?: "") &&
                                        (present.city?.trim() ?: "") == (permanent.city?.trim() ?: "") &&
                                        (present.pincode?.trim() ?: "") == (permanent.pincode?.trim() ?: "")
                                    
                                    if (areAddressesSame) {
                                        // Temporarily remove listener to avoid triggering copy/clear action
                                        addressEntryActivity?.cbCopyToAddressEntry?.setOnCheckedChangeListener(null)
                                        addressEntryActivity?.cbCopyToAddressEntry?.isChecked = true
                                        // Re-setup the checkbox listener only (TextWatchers are already set up)
                                        addressEntryActivity?.cbCopyToAddressEntry?.setOnCheckedChangeListener { _, isChecked ->
                                            if (isChecked) {
                                                // Copy current values when checkbox is checked
                                                copyPresentToPermanent()
                                            } else {
                                                // Clear permanent address fields when checkbox is unchecked
                                                addressEntryActivity?.etHouseNoPermanentAddressEntry?.text =
                                                    Editable.Factory.getInstance().newEditable("")
                                                addressEntryActivity?.etPermanentAddressEntry?.text =
                                                    Editable.Factory.getInstance().newEditable("")
                                                addressEntryActivity?.etLandmarkPermanentAddressEntry?.text =
                                                    Editable.Factory.getInstance().newEditable("")
                                                addressEntryActivity?.etCityPermanentAddressEntry?.text =
                                                    Editable.Factory.getInstance().newEditable("")
                                                addressEntryActivity?.etPincodePermanentAddressEntry?.text =
                                                    Editable.Factory.getInstance().newEditable("")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<MemberAddressInfoModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    throwable.printStackTrace()
                }
            })
        }
    }

    private fun navigateToNextStep() {
        (activity as? CreateMemberActivity)?.navigateToNextStep()
    }

    private fun addressEntryApi(addressEntryParams: AddressEntryParams) {
        if (isConnectingToInternet(requireContext())) {
            showProgressDialog()
            val call1 = ApiClient.buildService(activity).addressInfoApi(addressEntryParams)
            call1?.enqueue(object : Callback<AddressEntryModel?> {
                override fun onResponse(
                    call: Call<AddressEntryModel?>,
                    response: Response<AddressEntryModel?>
                ) {
                    hideProgressDialog()
                    isSubmitting = false
                    addressEntryActivity!!.btnSubmitAddressEntry.isEnabled = true
                    
                    if (response.isSuccessful) {
                        val addressEntryModel: AddressEntryModel? = response.body()
                        if (addressEntryModel != null) {
                            if (addressEntryModel.success == true) {
                                // Mark step as completed
                                MemberFlowManager.markStepCompleted(requireContext(), MemberFlowManager.FlowStep.ADDRESS)
                                
                                // Update stepper in parent activity
                                (activity as? CreateMemberActivity)?.updateStepper()
                                
                                // Show success message
                                CommonFunction.showToastSingle(requireContext(), 
                                    "Address details saved successfully", 0)
                                
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

                override fun onFailure(call: Call<AddressEntryModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    isSubmitting = false
                    addressEntryActivity!!.btnSubmitAddressEntry.isEnabled = true
                    
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