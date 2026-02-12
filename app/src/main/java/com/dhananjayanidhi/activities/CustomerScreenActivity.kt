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
import com.dhananjayanidhi.adapter.CustomerListV1Adapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityCustomerScreenBinding
import com.dhananjayanidhi.models.customerlist.CustomerListModel
import com.dhananjayanidhi.models.customerlist.DatumCustomerListModel
import com.dhananjayanidhi.models.customerlistv1.CustomerListV1Model
import com.dhananjayanidhi.models.customerlistv1.DatumCustomerListV1Model
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
    private var customerListV1Adapter: CustomerListV1Adapter? = null
    private var customerList = mutableListOf<DatumCustomerListModel>()

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

        getCustomerListV1()
// ADD THIS: Real-time search as user types
        customerScreenBinding?.etCustomerName?.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                filterList(query)
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    //        customerScreenBinding?.ivSearchList?.setOnClickListener {
//            if (TextUtils.isEmpty(customerScreenBinding?.etCustomerName?.text.toString().trim())) {
//                customerScreenBinding?.etCustomerName?.error =
//                    getString(R.string.enter_name)
//            } else {
//                val searchParams = SearchParams()
//                searchParams.search = customerScreenBinding?.etCustomerName?.text.toString()
//                customerSearchApi(searchParams)
//            }
//        }
    }
    private fun filterList(query: String) {
        val filteredList = mutableListOf<DatumCustomerListModel>()

        for (item in customerList) {
            // Search by Name or Mobile Number (adjust fields based on your model)
            if (item.customerName?.lowercase()?.contains(query) == true ||
                item.mobileNumber?.contains(query) == true) {
                filteredList.add(item)
            }
        }

        // Update the adapter with the filtered list
        if (customerListV1Adapter != null) {
            customerListV1Adapter?.updateList(filteredList)
        }
    }

    private fun getCustomerListV1() {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call = ApiClient.buildService(mContext).customerListV1Api()
            call?.enqueue(object : Callback<CustomerListModel?> {
                override fun onResponse(
                    call: Call<CustomerListModel?>,
                    response: Response<CustomerListModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val customerListV1Model = response.body()
                        if (customerListV1Model?.status == 200) {
                            customerList.clear()
                            customerListV1Model.data?.let { customerList.addAll(it) }
                            customerListV1Adapter = CustomerListV1Adapter(
                                customerList,
                                mContext!!, object : CustomerClickInterface {
                                    override fun onCustomerClick(customerId: String?, accountId: String?) {
                                        startActivity(
                                            Intent(
                                                mContext,
                                                CustomerDetailsScreenActivity::class.java
                                            ).putExtra(
                                                Constants.customerListId,
                                                customerId
                                            ).putExtra(Constants.searchText, "")
                                                .putExtra(
                                                    Constants.accountListId,
                                                    accountId
                                                )
                                        )
                                    }
                                })
                            customerScreenBinding?.rvCustomer?.adapter = customerListV1Adapter
                        } else {
                            AppController.instance?.sessionManager?.logoutUser()
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

                override fun onFailure(call: Call<CustomerListModel?>, t: Throwable) {
                    hideProgressDialog()
                    t.printStackTrace()
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