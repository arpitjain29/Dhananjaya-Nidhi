package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.LoanCustomerDetailsAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityLoanDetailsBinding
import com.dhananjayanidhi.models.loandetails.LoanDetailsModel
import com.dhananjayanidhi.parameters.CustomerDetailsParams
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
                                loanDetailsActivity?.tvLoanAmountLoanDetails?.text = String.format(
                                    "%s %s %s",
                                    "Loan Amount: ",getString(R.string.rs),
                                    loanDetailsModel.data?.account?.loanAmount
                                )
                                loanDetailsActivity?.tvPaidAmountLoanDetails?.text = String.format(
                                    "%s %s %s",
                                    "Paid Amount: ",getString(R.string.rs),
                                    loanDetailsModel.data?.account?.paidAmount
                                )
                                loanDetailsActivity?.tvDueDateLoanDetails?.text = String.format(
                                    "%s %s",
                                    "Due Date: ",
                                    CommonFunction.changeDateFormatFromAnother(loanDetailsModel.data?.account?.loanStartDate)
                                )
                                loanDetailsActivity?.tvPendingOutstandingAmountLoanDetails?.text =
                                    String.format(
                                        "%s %s %s",
                                        "O.A.: ",getString(R.string.rs),
                                        loanDetailsModel.data?.account?.outstandingAmount
                                    )
                                loanCustomerDetailsAdapter = mContext?.let {
                                    loanDetailsModel.data?.transactions?.let { it1 ->
                                        LoanCustomerDetailsAdapter(
                                            it1, it,
                                            object : LoanClickInterface {
                                                override fun onLoanClick(position: Int) {
                                                    startActivity(
                                                        Intent(
                                                            mContext,
                                                            LoanDetailsActivity::class.java
                                                        )
                                                    )
                                                }
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
}