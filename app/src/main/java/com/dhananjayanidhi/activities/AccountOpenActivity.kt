package com.dhananjayanidhi.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.CustomSpinnerAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityAccountOpenBinding
import com.dhananjayanidhi.databinding.SuccessFullPopupBinding
import com.dhananjayanidhi.models.accountopen.AccountOpenModel
import com.dhananjayanidhi.models.depositscheme.DepositSchemeModel
import com.dhananjayanidhi.parameters.AccountOpenParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import androidx.core.graphics.drawable.toDrawable

class AccountOpenActivity : BaseActivity() {
    private var accountOpenBinding: ActivityAccountOpenBinding? = null
    private var selectDepositAmount: String? = null
    private var getCustomerId: String? = null
    private var getMemberFees: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        accountOpenBinding = ActivityAccountOpenBinding.inflate(layoutInflater)
        setContentView(accountOpenBinding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply insets as margins instead of padding
            val layoutParams = v.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            v.layoutParams = layoutParams

            insets
        }

        accountOpenBinding?.appLayout?.ivMenu?.visibility = View.GONE
        accountOpenBinding?.appLayout?.ivBackArrow?.visibility = View.VISIBLE
        accountOpenBinding?.appLayout?.ivSearch?.visibility = View.GONE
        accountOpenBinding?.appLayout?.tvTitle?.text = getString(R.string.account_open)

        getCustomerId = intent.getStringExtra(Constants.customerIdGet)

        accountOpenBinding?.btnSubmitAccountOpen?.setOnClickListener {
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

            if (TextUtils.isEmpty(accountOpenParams.accountNumber)) {
                accountOpenBinding?.etAccountNumberOpen?.error =
                    getString(R.string.please_enter_account_no)
            } else if (TextUtils.isEmpty(accountOpenParams.memberFees)) {
                accountOpenBinding?.etMemberFeesOpen?.error =
                    getString(R.string.please_enter_member_fees)
            } else if (TextUtils.isEmpty(accountOpenParams.depositAmount)) {
                accountOpenBinding?.etDepositAmountOpen?.error =
                    getString(R.string.please_enter_deposit_amount)
            } else if (TextUtils.isEmpty(accountOpenParams.ddsAmount)) {
                accountOpenBinding?.etDdsAmountOpen?.error =
                    getString(R.string.please_enter_dds_amount)
            } else {
                openAccountApi(accountOpenParams)
            }
        }
        depositAmountApi()
    }

    private fun depositAmountApi() {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).depositSchemeApi()
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
//                                CommonFunction.showToastSingle(mContext!!,depositSchemeModel.message,0)
                                val adapter = depositSchemeModel.data?.schemes?.let {
                                    CustomSpinnerAdapter(
                                        mContext!!,
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
                                CommonFunction.showToastSingle(mContext, errorMessage, 0)
                                AppController.instance?.sessionManager?.logoutUser()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                AppController.instance?.sessionManager?.logoutUser()
                                CommonFunction.showToastSingle(
                                    mContext,
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
                mContext,
                resources.getString(R.string.net_connection), 0
            )
        }
    }

    private fun openAccountApi(accountOpenParams: AccountOpenParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).openAccountApi(accountOpenParams)
            call1?.enqueue(object : Callback<AccountOpenModel?> {
                override fun onResponse(
                    call: Call<AccountOpenModel?>,
                    response: Response<AccountOpenModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val accountOpenModel: AccountOpenModel? = response.body()
                        if (accountOpenModel != null) {
                            if (accountOpenModel.success == true) {
//                                CommonFunction.showToastSingle(
//                                    mContext!!,
//                                    accountOpenModel.message,
//                                    0
//                                )
                                accountOpenModel.message?.let { successFullyMsg(it) }
                            } else {
                                CommonFunction.showToastSingle(
                                    mContext!!,
                                    accountOpenModel.message,
                                    0
                                )
//                                AppController.instance?.sessionManager?.logoutUser()
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            try {
                                val errorJson = JSONObject(errorBody)
                                val errorArray = errorJson.getJSONArray("error")
                                val errorMessage = errorArray.getJSONObject(0).getString("message")
                                CommonFunction.showToastSingle(mContext, errorMessage, 0)
                                AppController.instance?.sessionManager?.logoutUser()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                AppController.instance?.sessionManager?.logoutUser()
                                CommonFunction.showToastSingle(
                                    mContext,
                                    "An error occurred. Please try again.",
                                    0
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<AccountOpenModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    throwable.printStackTrace()
                    if (throwable is HttpException) {
                        throwable.printStackTrace()
                    }
                }
            })
        } else {
            CommonFunction.showToastSingle(
                mContext,
                resources.getString(R.string.net_connection), 0
            )
        }
    }

    fun successFullyMsg(successFullMsg: String) {
        val dialog = Dialog(mContext!!, R.style.CustomAlertDialogStyle_space)
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
                mContext
            ), null, false
        )
        dialog.setContentView(binding.root)
        binding.tvMessageTextPopup.text = successFullMsg
        binding.tvYesTextPopup.setOnClickListener {
            startActivity(Intent(mContext!!, HomeActivity::class.java))
            dialog.dismiss()
        }
        dialog.show()
    }
}