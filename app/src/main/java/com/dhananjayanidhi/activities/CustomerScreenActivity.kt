package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.CustomerAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityCustomerScreenBinding
import com.dhananjayanidhi.models.customerlist.CustomerListModel
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

        customerScreenBinding!!.appLayout.ivMenu.visibility = View.GONE
        customerScreenBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        customerScreenBinding!!.appLayout.ivSearch.visibility = View.VISIBLE
        customerScreenBinding!!.appLayout.tvTitle.visibility = View.VISIBLE
        customerScreenBinding!!.appLayout.tvTitle.text = getString(R.string.customer_dds)
        customerScreenBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        customerListApi()
    }

    override fun onResume() {
        super.onResume()
        customerListApi()
    }

    private fun customerListApi() {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).customerListApi()
            call1?.enqueue(object : Callback<CustomerListModel?> {
                override fun onResponse(
                    call: Call<CustomerListModel?>,
                    response: Response<CustomerListModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val customerListModel: CustomerListModel? = response.body()
                        if (customerListModel != null) {
                            CommonFunction.showToastSingle(mContext, customerListModel.message, 0)
                            if (customerListModel.status == 200) {
                                customerAdapter = mContext?.let {
                                    customerListModel.data?.let { it1 ->
                                        CustomerAdapter(
                                            it1, it,
                                            object : CustomerClickInterface {
                                                override fun onCustomerClick(position: Int) {
                                                    startActivity(
                                                        Intent(
                                                            mContext,
                                                            CustomerDetailsScreenActivity::class.java
                                                        ).putExtra(Constants.customerListId,
                                                            customerListModel.data!![position].customerId
                                                        ).putExtra(Constants.searchText, "")
                                                            .putExtra(Constants.accountListId,customerListModel.data!![position].accountId)
//                                                            .putExtra(Constants.todayCollection,customerListModel.data!![position].todayCollectionStatus)
                                                    )
                                                }
                                            })
                                    }
                                }
                                customerScreenBinding!!.rvCustomer.adapter = customerAdapter
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

                override fun onFailure(call: Call<CustomerListModel?>, throwable: Throwable) {
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