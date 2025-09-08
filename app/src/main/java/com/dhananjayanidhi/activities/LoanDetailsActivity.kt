package com.dhananjayanidhi.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.LoanCustomerDetailsAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityLoanDetailsBinding
import com.dhananjayanidhi.databinding.EnterAmountLayoutBinding
import com.dhananjayanidhi.databinding.MsgPopupBinding
import com.dhananjayanidhi.models.loanamount.LoanAmountModel
import com.dhananjayanidhi.models.loandetails.LoanDetailsModel
import com.dhananjayanidhi.parameters.CustomerDetailsParams
import com.dhananjayanidhi.parameters.PaymentCollectionParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import com.dhananjayanidhi.utils.interfacef.LoanClickInterface
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class LoanDetailsActivity : BaseActivity() {
    private var loanDetailsActivity: ActivityLoanDetailsBinding? = null
    private var loanCustomerDetailsAdapter: LoanCustomerDetailsAdapter? = null
    private var customerId: String? = null
    private var accountId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loanDetailsActivity = ActivityLoanDetailsBinding.inflate(layoutInflater)
        setContentView(loanDetailsActivity!!.root)
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
        loanDetailsActivity!!.appLayout.ivMenu.visibility = View.GONE
        loanDetailsActivity!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        loanDetailsActivity!!.appLayout.ivSearch.visibility = View.GONE
        loanDetailsActivity!!.appLayout.tvTitle.text = getString(R.string.loan_detail)
        loanDetailsActivity!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
//        loanCustomerDetailsAdapter =
//            mContext?.let {
//                LoanCustomerDetailsAdapter(ArrayList(), it,object :LoanClickInterface{
//                    override fun onLoanClick(position: Int) {
//                        startActivity(Intent(mContext,EmiEntryActivity::class.java))
//                    }
//                })
//            }
//        loanDetailsActivity!!.rvLoanDetails.adapter = loanCustomerDetailsAdapter

        customerId = intent.getStringExtra(Constants.customerListId)
        accountId = intent.getStringExtra(Constants.accountListId)

        val customerDetailsParams = CustomerDetailsParams()
        customerDetailsParams.customerId = customerId
        customerDetailsParams.accountId = accountId
        loanListDetailsApi(customerDetailsParams)
    }

    override fun onResume() {
        super.onResume()
        val customerDetailsParams = CustomerDetailsParams()
        customerDetailsParams.customerId = customerId
        customerDetailsParams.accountId = accountId
        loanListDetailsApi(customerDetailsParams)
    }

    private fun loanListDetailsApi(customerDetailsParams: CustomerDetailsParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).loanListDetailsApi(customerDetailsParams)
            call1?.enqueue(object : Callback<LoanDetailsModel?> {
                override fun onResponse(
                    call: Call<LoanDetailsModel?>,
                    response: Response<LoanDetailsModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val loanDetailsModel: LoanDetailsModel? = response.body()
                        if (loanDetailsModel != null) {
                            CommonFunction.showToastSingle(mContext, loanDetailsModel.message, 0)
                            if (loanDetailsModel.status == 200) {
                                loanDetailsActivity?.tvNameLoanDetails?.text =
                                    loanDetailsModel.data?.account?.customerName
                                loanDetailsActivity?.tvEmiLoanDetails?.text = String.format(
                                    "%s %s %s",
                                    "Emi: ",getString(R.string.rs),
                                    loanDetailsModel.data?.account?.emi
                                )
                                loanDetailsActivity?.tvAccountNoLoanDetails?.text = String.format(
                                    "%s %s",
                                    "A/C : ",
                                    loanDetailsModel.data?.account?.accountNumber
                                )
                                if (loanDetailsModel.data?.account?.todayCollectionStatus == "yes") {
                                    loanDetailsActivity?.ivCheckCustomerLoanDetails?.visibility =
                                        View.VISIBLE
                                } else {
                                    loanDetailsActivity?.ivCheckCustomerLoanDetails?.visibility =
                                        View.GONE
                                }
//                                loanDetailsActivity?.tvPaidAmountLoanDetails?.text = String.format(
//                                    "%s %s %s",
//                                    "Paid Amount: ",getString(R.string.rs),
//                                    loanDetailsModel.data?.account?.paidAmount
//                                )
//                                loanDetailsActivity?.tvDueDateLoanDetails?.text = String.format(
//                                    "%s %s",
//                                    "Due Date: ",
//                                    CommonFunction.changeDateFormatFromAnother(loanDetailsModel.data?.account?.loanStartDate)
//                                )
//                                loanDetailsActivity?.tvPendingOutstandingAmountLoanDetails?.text =
//                                    String.format(
//                                        "%s %s %s",
//                                        "O.A.: ",getString(R.string.rs),
//                                        loanDetailsModel.data?.account?.outstandingAmount
//                                    )
                                loanDetailsActivity!!.fabViewClickLoanDetails.setOnClickListener {

                                    val dialog =
                                        Dialog(mContext!!, R.style.CustomAlertDialogStyle_space)
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
                                    dialog.setCancelable(true)
                                    val binding: EnterAmountLayoutBinding =
                                        EnterAmountLayoutBinding.inflate(
                                            LayoutInflater.from(
                                                mContext
                                            ), null, false
                                        )
                                    dialog.setContentView(binding.root)
                                    binding.btnCollect.setOnClickListener {
                                        if (TextUtils.isEmpty(
                                                binding.etAmountCustomer.text.toString().trim()
                                            )
                                        ) {
                                            binding.etAmountCustomer.error =
                                                getString(R.string.please_enter_amount)
                                        } else {
                                            dialog.dismiss()
                                            val paymentCollectionParams =
                                                PaymentCollectionParams()
                                            paymentCollectionParams.customerId =
                                                loanDetailsModel.data?.account?.customerId
                                            paymentCollectionParams.accountId =
                                                loanDetailsModel.data?.account?.id
                                            paymentCollectionParams.amount =
                                                binding.etAmountCustomer.text.toString().trim()
                                            customerLoanAddAmountApi(paymentCollectionParams)
                                        }
                                    }
                                    dialog.show()
                                }
                                loanCustomerDetailsAdapter = mContext?.let {
                                    loanDetailsModel.data?.transactions?.let { it1 ->
                                        LoanCustomerDetailsAdapter(
                                            it1, it,
                                            object : LoanClickInterface {
                                                override fun onLoanClick(position: Int) {}
                                            })
                                    }
                                }
                                loanDetailsActivity!!.rvLoanDetails.adapter =
                                    loanCustomerDetailsAdapter
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

                override fun onFailure(call: Call<LoanDetailsModel?>, throwable: Throwable) {
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

    private fun customerLoanAddAmountApi(paymentCollectionParams: PaymentCollectionParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 =
                ApiClient.buildService(mContext).loanAmountAddApi(paymentCollectionParams)
            call1?.enqueue(object : Callback<LoanAmountModel?> {
                override fun onResponse(
                    call: Call<LoanAmountModel?>,
                    response: Response<LoanAmountModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val loanAmountModel: LoanAmountModel? = response.body()
                        if (loanAmountModel != null) {
                            if (loanAmountModel.status == 200) {
                                CommonFunction.showToastSingle(
                                    mContext,
                                    loanAmountModel.message,
                                    0
                                )
                                val customerDetailsParams = CustomerDetailsParams()
                                customerDetailsParams.customerId = customerId
                                customerDetailsParams.accountId = accountId
                                loanListDetailsApi(customerDetailsParams)
                                loanCustomerDetailsAdapter?.notifyDataSetChanged()
                                startActivity(Intent(mContext,HomeActivity::class.java))
                                finish()
                            } else if (loanAmountModel.status == 202) {
                                showDialog(loanAmountModel.message)
                            } else {
                                showDialog(loanAmountModel.message)
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

                override fun onFailure(call: Call<LoanAmountModel?>, throwable: Throwable) {
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

    fun showDialog(msg: String?) {
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
        dialog.setCancelable(true)
        val binding: MsgPopupBinding = MsgPopupBinding.inflate(
            LayoutInflater.from(
                mContext
            ), null, false
        )
        dialog.setContentView(binding.root)
        binding.tvMessageTextPopup.text = msg

        binding.tvYesTextPopup.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(mContext,HomeActivity::class.java))
            finish()
        }
        dialog.show()
    }
}