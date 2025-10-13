package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.CustomerSearchAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityCustomerSearchBinding
import com.dhananjayanidhi.models.customersearch.CustomerSearchModel
import com.dhananjayanidhi.models.loansearch.LoanSearchModel
import com.dhananjayanidhi.models.search.SearchModel
import com.dhananjayanidhi.parameters.CustomerSearchParams
import com.dhananjayanidhi.parameters.SearchParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class CustomerSearchActivity : BaseActivity() {
    private var customerSearchBinding: ActivityCustomerSearchBinding? = null
    private var customerSearchAdapter: CustomerSearchAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        customerSearchBinding = ActivityCustomerSearchBinding.inflate(layoutInflater)
        setContentView(customerSearchBinding?.root)
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

        customerSearchBinding!!.appLayout.ivMenu.visibility = View.GONE
        customerSearchBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        customerSearchBinding!!.appLayout.ivSearch.visibility = View.GONE
        customerSearchBinding!!.appLayout.tvTitle.visibility = View.GONE
        customerSearchBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

//        customerSearchBinding?.etCustomerName?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                val query = s.toString().trim()
//                if (query.length >= 3) {
//                    val searchParams = SearchParams()
//                    searchParams.search = query
//                    customerSearchApi(searchParams)
//                }
//            }
//        })
    }

//    private fun customerSearchApi(searchParams: SearchParams) {
//        if (isConnectingToInternet(mContext!!)) {
//            showProgressDialog()
//            val call1 = ApiClient.buildService(mContext).searchCustomerNameApi(searchParams)
//            call1.enqueue(object : Callback<CustomerSearchModel> {
//                override fun onResponse(
//                    call: Call<CustomerSearchModel>,
//                    response: Response<CustomerSearchModel>
//                ) {
//                    hideProgressDialog()
//                    if (response.isSuccessful) {
//                        val customerSearchModel: CustomerSearchModel? = response.body()
//                        if (customerSearchModel != null) {
//                            if (customerSearchModel.status == 200) {
//                                customerSearchAdapter = customerSearchModel.data?.let {
//                                    CustomerSearchAdapter(
//                                        it,
//                                        mContext!!, object : CustomerClickInterface {
//                                            override fun onCustomerClick(position: Int) {
//                                                startActivity(
//                                                    Intent(
//                                                        mContext,
//                                                        CustomerScreenActivity::class.java
//                                                    ).putExtra("customerList",ArrayList(customerSearchModel.data!!))
//                                                )
//                                            }
//                                        })
//                                }
//                                customerSearchBinding?.rvCustomerSearch?.adapter = customerSearchAdapter
//                            } else {
//                                AppController.instance?.sessionManager?.logoutUser()
//                            }
//                        }
//                    } else {
//                        val errorBody = response.errorBody()?.string()
//                        if (errorBody != null) {
//                            try {
//                                val errorJson = JSONObject(errorBody)
//                                val errorArray = errorJson.getJSONArray("error")
//                                val errorMessage = errorArray.getJSONObject(0).getString("message")
//                                CommonFunction.showToastSingle(mContext, errorMessage, 0)
//                                AppController.instance?.sessionManager?.logoutUser()
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                                AppController.instance?.sessionManager?.logoutUser()
//                                CommonFunction.showToastSingle(
//                                    mContext,
//                                    "An error occurred. Please try again.",
//                                    0
//                                )
//                            }
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<CustomerSearchModel>, throwable: Throwable) {
//                    hideProgressDialog()
//                    throwable.printStackTrace()
//                    if (throwable is HttpException) {
//                        throwable.printStackTrace()
//                    }
//                }
//            })
//        } else {
//            CommonFunction.showToastSingle(
//                mContext,
//                resources.getString(R.string.net_connection), 0
//            )
//        }
//    }
}