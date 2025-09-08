package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.CustomerAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityCustomerScreenBinding
import com.dhananjayanidhi.models.customerlist.CustomerListModel
import com.dhananjayanidhi.models.customerlistv1.CustomerListV1Model
import com.dhananjayanidhi.models.customerlistv1.DatumCustomerListV1Model
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import com.dhananjayanidhi.utils.PaginationScrollListener
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class CustomerScreenActivity : BaseActivity() {
    private var customerScreenBinding: ActivityCustomerScreenBinding? = null
    private var customerAdapter: CustomerAdapter? = null
    private var isLoading = false
    var datumCustomerListV1Model: MutableList<DatumCustomerListV1Model>? = ArrayList()
    var totalPage: String? = null
    var nextPageUrl: String? = null
    var currentPage: String? = null

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
        customerScreenBinding?.appLayout?.ivSearch?.visibility = View.VISIBLE
        customerScreenBinding?.appLayout?.tvTitle?.visibility = View.VISIBLE
        customerScreenBinding?.appLayout?.tvTitle?.text = getString(R.string.customer_dds)
        customerScreenBinding?.appLayout?.ivBackArrow?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val llManger = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        customerScreenBinding?.rvCustomer?.layoutManager = llManger
        customerAdapter =
            datumCustomerListV1Model?.let {
                CustomerAdapter(it, mContext!!, object : CustomerClickInterface {
                    override fun onCustomerClick(position: Int) {
                        startActivity(
                            Intent(
                                mContext,
                                CustomerDetailsScreenActivity::class.java
                            ).putExtra(Constants.customerListId,
                                datumCustomerListV1Model!![position].customerId
                            ).putExtra(Constants.searchText, "")
                                .putExtra(Constants.accountListId,datumCustomerListV1Model!![position].accountId)
                        )
                    }
                })
            }
        customerScreenBinding?.rvCustomer?.adapter = customerAdapter

        customerScreenBinding?.rvCustomer?.addOnScrollListener(object : PaginationScrollListener(llManger) {
            override val isLoading: Boolean
                get() = this@CustomerScreenActivity.isLoading
            override val isLastPage: Boolean
                get() = nextPageUrl == null

            override fun loadMoreItems() {
                this@CustomerScreenActivity.isLoading = true
                customerListPageApi()
                this@CustomerScreenActivity.isLoading = false
            }
        })
        this.isLoading = false
        customerListApi()
    }

    private fun customerListApi() {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).customerListV1Api()
            call1?.enqueue(object : Callback<CustomerListV1Model?> {
                override fun onResponse(
                    call: Call<CustomerListV1Model?>,
                    response: Response<CustomerListV1Model?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val customerListV1Model: CustomerListV1Model? = response.body()
                        if (customerListV1Model != null) {
                            CommonFunction.showToastSingle(mContext, customerListV1Model.message, 0)
//                            customerAdapter =
//                                customerListV1Model.data?.let {
//                                    CustomerAdapter(
//                                        it,
//                                        mContext!!,
//                                        object : CustomerClickInterface {
//                                            override fun onCustomerClick(position: Int) {
//                                                startActivity(
//                                                    Intent(
//                                                        mContext,
//                                                        CustomerDetailsScreenActivity::class.java
//                                                    ).putExtra(
//                                                        Constants.customerListId,
//                                                        customerListV1Model.data!![position].customerId
//                                                    ).putExtra(Constants.searchText, "")
//                                                        .putExtra(
//                                                            Constants.accountListId,
//                                                            customerListV1Model.data!![position].accountId
//                                                        )
//                                                )
//                                            }
//                                        })
//                                }
//                            customerScreenBinding!!.rvCustomer.adapter = customerAdapter
                            if (customerListV1Model.status == 200) {
                                if (customerListV1Model.data?.data?.isNotEmpty() == true) {
                                    datumCustomerListV1Model?.clear()
                                    customerListV1Model.data!!.data?.let {
                                        datumCustomerListV1Model?.addAll(
                                            it
                                        )
                                    }
                                    customerAdapter!!.notifyDataSetChanged()
                                    isLoading = false
                                } else {
                                    ArrayList<DatumCustomerListV1Model>()
                                }

                                nextPageUrl = customerListV1Model.data?.nextPageUrl
                                totalPage = customerListV1Model.data?.lastPage
                                currentPage = customerListV1Model.data?.currentPage

                                if (datumCustomerListV1Model != null && datumCustomerListV1Model?.isNotEmpty() == true) {
                                    customerScreenBinding?.tvNoRecordFound?.visibility = View.GONE
                                    customerScreenBinding?.rvCustomer?.visibility = View.VISIBLE
                                } else {
                                    customerScreenBinding?.tvNoRecordFound?.visibility = View.VISIBLE
                                    customerScreenBinding?.rvCustomer?.visibility = View.GONE
                                }
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

                override fun onFailure(call: Call<CustomerListV1Model?>, throwable: Throwable) {
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

    private fun customerListPageApi() {
        if (nextPageUrl == null) return
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = nextPageUrl?.let { ApiClient.buildService(mContext).getCustomerList(it) }
            call1?.enqueue(object : Callback<CustomerListV1Model?> {
                override fun onResponse(
                    call: Call<CustomerListV1Model?>,
                    response: Response<CustomerListV1Model?>
                ) {
                    hideProgressDialog()
                    isLoading = false
                    if (response.isSuccessful) {
                        val customerListV1Model: CustomerListV1Model? = response.body()
                        if (customerListV1Model != null) {
                            CommonFunction.showToastSingle(mContext, customerListV1Model.message, 0)
                            if (customerListV1Model.status == 200) {
                                if (customerListV1Model.data?.data?.isNotEmpty() == true) {

                                    customerListV1Model.data!!.data?.let {
                                        datumCustomerListV1Model?.addAll(
                                            it
                                        )
                                    }
                                    customerAdapter!!.notifyDataSetChanged()
                                    isLoading = false
                                } else {
                                    ArrayList<DatumCustomerListV1Model>()
                                }
                                nextPageUrl = customerListV1Model.data?.nextPageUrl
                                currentPage = customerListV1Model.data?.currentPage

                                if (datumCustomerListV1Model != null && datumCustomerListV1Model?.isNotEmpty() == true) {
                                    customerScreenBinding?.tvNoRecordFound?.visibility = View.GONE
                                    customerScreenBinding?.rvCustomer?.visibility = View.VISIBLE
                                } else {
                                    customerScreenBinding?.tvNoRecordFound?.visibility = View.VISIBLE
                                    customerScreenBinding?.rvCustomer?.visibility = View.GONE
                                }
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

                override fun onFailure(call: Call<CustomerListV1Model?>, throwable: Throwable) {
                    hideProgressDialog()
                    isLoading = false
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