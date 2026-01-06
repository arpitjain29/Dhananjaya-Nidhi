package com.dhananjayanidhi.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.LinearLayout
import android.view.LayoutInflater
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.CustomSpinnerAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityAccountOpenBinding
import com.dhananjayanidhi.databinding.SuccessFullPopupBinding
import com.dhananjayanidhi.models.accountopen.AccountOpenModel
import com.dhananjayanidhi.models.depositscheme.DepositSchemeModel
import com.dhananjayanidhi.parameters.AccountOpenParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseFragment
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.MemberFlowManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import androidx.core.graphics.drawable.toDrawable

class AccountOpenActivity: BaseFragment() {
    private var accountOpenBinding: ActivityAccountOpenBinding? = null
    private var selectDepositAmount: String? = null
    private var getCustomerId: String? = null
    private var getMemberFees: String? = null
    private var isSubmitting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountOpenBinding = ActivityAccountOpenBinding.inflate(inflater, container, false)
        return accountOpenBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Validate step access
        if (!MemberFlowManager.canAccessStep(requireContext(), MemberFlowManager.FlowStep.ACCOUNT)) {
            CommonFunction.showToastSingle(requireContext(), "Please complete previous steps first", 0)
            (activity as? CreateMemberActivity)?.navigateToPreviousStep()
            return
        }

        // Get customer ID from flow manager
        getCustomerId = MemberFlowManager.getCustomerId(requireContext())
        
        if (getCustomerId.isNullOrEmpty()) {
            CommonFunction.showToastSingle(requireContext(), "Customer ID not found. Please start from beginning.", 0)
            (activity as? CreateMemberActivity)?.navigateToPreviousStep()
            return
        }
        
        // Check if step is already completed (resume flow)
        // Removed auto-navigation - allow user to view/edit completed steps when navigating back

        // Add TextWatchers to clear errors when user types
        setupTextWatchers()
        
        accountOpenBinding?.btnSubmitAccountOpen?.setOnClickListener {
            // Prevent multiple clicks
            if (isSubmitting) return@setOnClickListener
            
            val accountOpenParams = AccountOpenParams()
            accountOpenParams.customerId = getCustomerId
            accountOpenParams.schemeId = selectDepositAmount
            accountOpenParams.accountNumber =
                accountOpenBinding?.etAccountNumberOpen?.text.toString().trim()
            accountOpenParams.memberFees = getMemberFees
            accountOpenParams.depositAmount =
                accountOpenBinding?.etDepositAmountOpen?.text.toString().trim()
            accountOpenParams.ddsAmount =
                accountOpenBinding?.etDdsAmountOpen?.text.toString().trim()

            // Clear all previous errors
            accountOpenBinding?.tilAccountNumberOpen?.apply {
                error = null
                isErrorEnabled = false
            }
            accountOpenBinding?.tilMemberFeesOpen?.apply {
                error = null
                isErrorEnabled = false
            }
            accountOpenBinding?.tilDepositAmountOpen?.apply {
                error = null
                isErrorEnabled = false
            }
            accountOpenBinding?.tilDdsAmountOpen?.apply {
                error = null
                isErrorEnabled = false
            }
            
            var hasError = false
            
            if (TextUtils.isEmpty(accountOpenParams.accountNumber)) {
                accountOpenBinding?.tilAccountNumberOpen?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_account_no)
                }
                hasError = true
            } else if (accountOpenParams.accountNumber!!.length < 8 || accountOpenParams.accountNumber!!.length > 20) {
                accountOpenBinding?.tilAccountNumberOpen?.apply {
                    isErrorEnabled = true
                    error = "Account number must be between 8 and 20 characters"
                }
                hasError = true
            }
            if (TextUtils.isEmpty(accountOpenParams.memberFees)) {
                accountOpenBinding?.tilMemberFeesOpen?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_member_fees)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(accountOpenParams.depositAmount)) {
                accountOpenBinding?.tilDepositAmountOpen?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_deposit_amount)
                }
                hasError = true
            }
            if (TextUtils.isEmpty(accountOpenParams.ddsAmount)) {
                accountOpenBinding?.tilDdsAmountOpen?.apply {
                    isErrorEnabled = true
                    error = getString(R.string.please_enter_dds_amount)
                }
                hasError = true
            }
            
            if (!hasError) {
                isSubmitting = true
                accountOpenBinding?.btnSubmitAccountOpen?.isEnabled = false
                openAccountApi(accountOpenParams)
            }
        }
        depositAmountApi()
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
        
        accountOpenBinding?.etAccountNumberOpen?.addTextChangedListener(
            createErrorClearingWatcher(accountOpenBinding?.tilAccountNumberOpen)
        )
        accountOpenBinding?.etDepositAmountOpen?.addTextChangedListener(
            createErrorClearingWatcher(accountOpenBinding?.tilDepositAmountOpen)
        )
        accountOpenBinding?.etDdsAmountOpen?.addTextChangedListener(
            createErrorClearingWatcher(accountOpenBinding?.tilDdsAmountOpen)
        )
    }
    
    private fun navigateToNextStep() {
        // Complete the flow - CreateMemberActivity will handle navigation to home
        MemberFlowManager.completeFlow(requireContext())
        CommonFunction.showToastSingle(requireContext(), "Member creation completed successfully!", 0)
        (activity as? CreateMemberActivity)?.navigateToNextStep()
    }

    private fun depositAmountApi() {
        if (isConnectingToInternet(requireContext())) {
            showProgressDialog()
            val call1 = ApiClient.buildService(activity).depositSchemeApi()
            call1?.enqueue(object : Callback<DepositSchemeModel?> {
                override fun onResponse(
                    call: Call<DepositSchemeModel?>,
                    response: Response<DepositSchemeModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val depositSchemeModel: DepositSchemeModel? = response.body()
                        if (depositSchemeModel != null) {
                            if (depositSchemeModel.success == true) {
//                                CommonFunction.showToastSingle(requireContext(),depositSchemeModel.message,0)
                                val adapter = depositSchemeModel.data?.schemes?.let {
                                    CustomSpinnerAdapter(
                                        requireContext(),
                                        it
                                    )
                                }
                                accountOpenBinding?.spAccountOpen?.adapter = adapter

                                accountOpenBinding?.spAccountOpen?.onItemSelectedListener =
                                    object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            parent: AdapterView<*>,
                                            view: View?,
                                            position: Int,
                                            id: Long
                                        ) {
                                            selectDepositAmount =
                                                depositSchemeModel.data?.schemes?.get(position)?.id
                                            getMemberFees = depositSchemeModel.data?.memberFees
                                            accountOpenBinding?.etMemberFeesOpen?.text =
                                                Editable.Factory.getInstance()
                                                    .newEditable(getMemberFees)
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>) {
                                            // Do nothing
                                        }
                                    }
                            } else {
                                AppController.instance?.sessionManager?.logoutUser()
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

                override fun onFailure(call: Call<DepositSchemeModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    throwable.printStackTrace()
                    if (throwable is HttpException) {
                        throwable.printStackTrace()
                    }
                }
            })
        } else {
            CommonFunction.showToastSingle(
                requireContext(),
                resources.getString(R.string.net_connection), 0
            )
        }
    }

    private fun openAccountApi(accountOpenParams: AccountOpenParams) {
        if (isConnectingToInternet(requireContext())) {
            showProgressDialog()
            val call1 = ApiClient.buildService(activity).openAccountApi(accountOpenParams)
            call1?.enqueue(object : Callback<AccountOpenModel?> {
                override fun onResponse(
                    call: Call<AccountOpenModel?>,
                    response: Response<AccountOpenModel?>
                ) {
                    hideProgressDialog()
                    isSubmitting = false
                    accountOpenBinding?.btnSubmitAccountOpen?.isEnabled = true
                    
                    if (response.isSuccessful) {
                        val accountOpenModel: AccountOpenModel? = response.body()
                        if (accountOpenModel != null) {
                            if (accountOpenModel.success == true) {
                                // Mark step as completed
                                MemberFlowManager.markStepCompleted(requireContext(), MemberFlowManager.FlowStep.ACCOUNT)
                                
                                // Update stepper in parent activity
                                (activity as? CreateMemberActivity)?.updateStepper()
                                
                                // Show success message via dialog
                                accountOpenModel.message?.let { successFullyMsg(it) }
                            } else {
                                // Show API error message when success is false
                                val errorMsg = accountOpenModel.message
                                if (!errorMsg.isNullOrEmpty()) {
                                    CommonFunction.showToastSingle(requireContext(), errorMsg, 0)
                                } else {
                                    CommonFunction.showToastSingle(requireContext(), "Failed to open account", 0)
                                }
                            }
                        } else {
                            // Response body is null, try to parse error from errorBody
                            try {
                                val errorBody = response.errorBody()?.string()
                                if (!errorBody.isNullOrEmpty()) {
                                    val errorJson = JSONObject(errorBody)
                                    val errorMessage = errorJson.optString("message", null)
                                        ?: errorJson.optJSONArray("error")?.getJSONObject(0)?.optString("message", null)
                                    
                                    if (!errorMessage.isNullOrEmpty()) {
                                        CommonFunction.showToastSingle(requireContext(), errorMessage, 0)
                                    } else {
                                        CommonFunction.showToastSingle(
                                            requireContext(),
                                            "An error occurred. Please try again.",
                                            0
                                        )
                                    }
                                } else {
                                    CommonFunction.showToastSingle(
                                        requireContext(),
                                        "An error occurred. Please try again.",
                                        0
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                CommonFunction.showToastSingle(
                                    requireContext(),
                                    "An error occurred. Please try again.",
                                    0
                                )
                            }
                        }
                    } else {
                        // HTTP error response
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                val errorJson = JSONObject(errorBody)
                                val errorMessage = errorJson.optString("message", null)
                                    ?: errorJson.optJSONArray("error")?.getJSONObject(0)?.optString("message", null)
                                
                                if (!errorMessage.isNullOrEmpty()) {
                                    CommonFunction.showToastSingle(requireContext(), errorMessage, 0)
                                } else {
                                    CommonFunction.showToastSingle(
                                        requireContext(),
                                        "An error occurred. Please try again.",
                                        0
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                CommonFunction.showToastSingle(
                                    requireContext(),
                                    "An error occurred. Please try again.",
                                    0
                                )
                            }
                        } else {
                            CommonFunction.showToastSingle(
                                requireContext(),
                                "An error occurred. Please try again.",
                                0
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<AccountOpenModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    isSubmitting = false
                    accountOpenBinding?.btnSubmitAccountOpen?.isEnabled = true
                    
                    throwable.printStackTrace()
                    // Only show network error for actual network failures, not API errors
                    if (throwable is retrofit2.HttpException) {
                        // Try to parse error message from HTTP exception
                        try {
                            val errorBody = throwable.response()?.errorBody()?.string()
                            if (!errorBody.isNullOrEmpty()) {
                                val errorJson = JSONObject(errorBody)
                                val errorMessage = errorJson.optString("message", null)
                                    ?: errorJson.optJSONArray("error")?.getJSONObject(0)?.optString("message", null)
                                
                                if (!errorMessage.isNullOrEmpty()) {
                                    CommonFunction.showToastSingle(requireContext(), errorMessage, 0)
                                    return
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    // Show network error only for actual network failures
//                    CommonFunction.showToastSingle(
//                        requireContext(),
//                        "Network error. Please check your connection and try again.",
//                        0
//                    )
                }
            })
        } else {
            CommonFunction.showToastSingle(
                requireContext(),
                resources.getString(R.string.net_connection), 0
            )
        }
    }

    fun successFullyMsg(successFullMsg: String) {
        val dialog = Dialog(requireContext(), R.style.CustomAlertDialogStyle_space)
        if (dialog.window != null) {
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setGravity(Gravity.CENTER)
        }
        if (dialog.window != null) {
            dialog.window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(
                Color.TRANSPARENT.toDrawable()
            )
        }
        dialog.setCancelable(false)
        val binding: SuccessFullPopupBinding = SuccessFullPopupBinding.inflate(
            LayoutInflater.from(
                requireContext()
            ), null, false
        )
        dialog.setContentView(binding.root)
        binding.tvMessageTextPopup.text = successFullMsg
        binding.tvYesTextPopup.setOnClickListener {
            // Complete the flow - CreateMemberActivity will handle navigation
            MemberFlowManager.completeFlow(requireContext())
            (activity as? CreateMemberActivity)?.navigateToNextStep()
            dialog.dismiss()
        }
        dialog.show()
    }
}