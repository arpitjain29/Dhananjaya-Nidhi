package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.LoanCustomerAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityLoanScreenBinding
import com.dhananjayanidhi.models.loansearch1.LoanSearch1Model
import com.dhananjayanidhi.parameters.SearchParams
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

class LoanScreenActivity : BaseActivity() {
    private var loanScreenBinding: ActivityLoanScreenBinding? = null
    private var loanCustomerAdapter: LoanCustomerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loanScreenBinding = ActivityLoanScreenBinding.inflate(layoutInflater)
        setContentView(loanScreenBinding!!.root)
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
        loanScreenBinding!!.appLayout.ivMenu.visibility = View.GONE
        loanScreenBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        loanScreenBinding!!.appLayout.ivSearch.visibility = View.GONE
        loanScreenBinding!!.appLayout.tvTitle.visibility = View.VISIBLE
        loanScreenBinding!!.appLayout.tvTitle.text = getString(R.string.loan)
        loanScreenBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        loanScreenBinding?.ivSearchList?.setOnClickListener {
            if (TextUtils.isEmpty(loanScreenBinding?.etCustomerName?.text.toString().trim())) {
                loanScreenBinding?.etCustomerName?.error =
                    getString(R.string.enter_name)
            } else {
                val searchParams = SearchParams()
                searchParams.search = loanScreenBinding?.etCustomerName?.text.toString()
                loanSearchApi(searchParams)
            }
        }
    }

    private fun loanSearchApi(searchParams: SearchParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).searchLoanNameApi(searchParams)
            call1.enqueue(object : Callback<LoanSearch1Model> {
                override fun onResponse(
                    call: Call<LoanSearch1Model>,
                    response: Response<LoanSearch1Model>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val loanSearch1Model: LoanSearch1Model? = response.body()
                        if (loanSearch1Model != null) {
                            if (loanSearch1Model.status == 200) {
                                loanCustomerAdapter = loanSearch1Model.data?.let {
                                    LoanCustomerAdapter(
                                        it,
                                        mContext!!, object : LoanClickInterface {
                                            override fun onLoanClick(position: Int) {
                                                startActivity(
                                                    Intent(
                                                        mContext,
                                                        CustomerDetailsScreenActivity::class.java
                                                    ).putExtra(
                                                        Constants.customerListId,
                                                        loanSearch1Model.data!![position].customerId
                                                    ).putExtra(Constants.searchText, "")
                                                        .putExtra(
                                                            Constants.accountListId,
                                                            loanSearch1Model.data!![position].id
                                                        )
                                                )
                                            }
                                        })
                                }
                                loanScreenBinding?.rvLoanCustomer?.adapter = loanCustomerAdapter
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

                override fun onFailure(call: Call<LoanSearch1Model>, throwable: Throwable) {
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