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
import com.dhananjayanidhi.adapter.CustomerAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityCustomerScreenBinding
import com.dhananjayanidhi.models.customersearch.CustomerSearchModel
import com.dhananjayanidhi.parameters.SearchParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class CustomerScreenActivity : BaseActivity() {
    private var customerScreenBinding: ActivityCustomerScreenBinding? = null
    private var customerAdapter: CustomerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        customerScreenBinding = ActivityCustomerScreenBinding.inflate(layoutInflater)
        setContentView(customerScreenBinding!!.root)
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

        customerScreenBinding?.appLayout?.ivMenu?.visibility = View.GONE
        customerScreenBinding?.appLayout?.ivBackArrow?.visibility = View.VISIBLE
        customerScreenBinding?.appLayout?.ivSearch?.visibility = View.GONE
        customerScreenBinding?.appLayout?.tvTitle?.visibility = View.VISIBLE
        customerScreenBinding?.appLayout?.tvTitle?.text = getString(R.string.customer_dds)
        customerScreenBinding?.appLayout?.ivBackArrow?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        customerScreenBinding?.ivSearchList?.setOnClickListener {
            if (TextUtils.isEmpty(customerScreenBinding?.etCustomerName?.text.toString().trim())) {
                customerScreenBinding?.etCustomerName?.error =
                    getString(R.string.enter_name)
            } else {
                val searchParams = SearchParams()
                searchParams.search = customerScreenBinding?.etCustomerName?.text.toString()
                customerSearchApi(searchParams)
            }
        }
    }

    private fun customerSearchApi(searchParams: SearchParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).searchCustomerNameApi(searchParams)
            call1.enqueue(object : Callback<CustomerSearchModel> {
                override fun onResponse(
                    call: Call<CustomerSearchModel>,
                    response: Response<CustomerSearchModel>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val customerSearchModel: CustomerSearchModel? = response.body()
                        if (customerSearchModel != null) {
                            if (customerSearchModel.status == 200) {
                                customerAdapter = customerSearchModel.data?.let {
                                    CustomerAdapter(
                                        it,
                                        mContext!!, object : CustomerClickInterface {
                                            override fun onCustomerClick(position: Int) {
                                                startActivity(
                                                    Intent(
                                                        mContext,
                                                        CustomerDetailsScreenActivity::class.java
                                                    ).putExtra(
                                                        Constants.customerListId,
                                                        customerSearchModel.data!![position].customerId
                                                    ).putExtra(Constants.searchText, "")
                                                        .putExtra(
                                                            Constants.accountListId,
                                                            customerSearchModel.data!![position].accountId
                                                        )
                                                )
                                            }
                                        })
                                }
                                customerScreenBinding?.rvCustomer?.adapter = customerAdapter
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

                override fun onFailure(call: Call<CustomerSearchModel>, throwable: Throwable) {
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