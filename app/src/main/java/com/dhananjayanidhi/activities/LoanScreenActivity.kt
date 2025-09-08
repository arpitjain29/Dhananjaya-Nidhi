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
import com.dhananjayanidhi.adapter.LoanCustomerAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityLoanScreenBinding
import com.dhananjayanidhi.models.customerlistv1.DatumCustomerListV1Model
import com.dhananjayanidhi.models.loanlist.DatumLoanListModel
import com.dhananjayanidhi.models.loanlist.LoanListModel
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import com.dhananjayanidhi.utils.PaginationScrollListener
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface
import com.dhananjayanidhi.utils.interfacef.LoanClickInterface
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class LoanScreenActivity : BaseActivity() {
    private var loanScreenBinding: ActivityLoanScreenBinding? = null
    private var loanCustomerAdapter: LoanCustomerAdapter? = null

    private var isLoading = false
    var datumLoanListModel: MutableList<DatumLoanListModel>? = ArrayList()
    var totalPage: String? = null
    var nextPageUrl: String? = null
    var currentPage: String? = null

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
        loanScreenBinding!!.appLayout.ivSearch.visibility = View.VISIBLE
        loanScreenBinding!!.appLayout.tvTitle.visibility = View.VISIBLE
        loanScreenBinding!!.appLayout.tvTitle.text = getString(R.string.loan)
        loanScreenBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
//        loanCustomerAdapter = mContext?.let {
//            LoanCustomerAdapter(ArrayList(), it, object : LoanClickInterface {
//                override fun onLoanClick(position: Int) {
//                    startActivity(Intent(mContext, LoanDetailsActivity::class.java))
//                }
//            })
//        }
//        loanScreenBinding!!.rvLoanCustomer.adapter = loanCustomerAdapter
        val llManger = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        loanScreenBinding!!.rvLoanCustomer.layoutManager = llManger
        loanCustomerAdapter =
            datumLoanListModel?.let {
                LoanCustomerAdapter(it, mContext!!, object : LoanClickInterface {
                    override fun onLoanClick(position: Int) {
                        startActivity(
                            Intent(
                                mContext,
                                LoanDetailsActivity::class.java
                            ).putExtra(
                                Constants.customerListId,
                                datumLoanListModel!![position].customerId
                            ).putExtra(
                                Constants.accountListId,
                                datumLoanListModel!![position].id
                            )
                        )
                    }
                })
            }
        loanScreenBinding!!.rvLoanCustomer.adapter = loanCustomerAdapter

        loanScreenBinding!!.rvLoanCustomer.addOnScrollListener(object : PaginationScrollListener(llManger) {
            override val isLoading: Boolean
                get() = this@LoanScreenActivity.isLoading
            override val isLastPage: Boolean
                get() = currentPage!! >= totalPage!!

            override fun loadMoreItems() {
                this@LoanScreenActivity.isLoading = true
                loanListPageApi()
                this@LoanScreenActivity.isLoading = false
            }
        })
        this.isLoading = false
        loanListApi()
    }

    override fun onResume() {
        super.onResume()
        loanListApi()
    }

    private fun loanListApi() {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).loanListApi()
            call1?.enqueue(object : Callback<LoanListModel?> {
                override fun onResponse(
                    call: Call<LoanListModel?>,
                    response: Response<LoanListModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val loanListModel: LoanListModel? = response.body()
                        if (loanListModel != null) {
                            CommonFunction.showToastSingle(mContext, loanListModel.message, 0)
                            if (loanListModel.status == 200) {
//                                loanCustomerAdapter = mContext?.let {
//                                    loanListModel.data?.data?.let { it1 ->
//                                        LoanCustomerAdapter(
//                                            it1, it,
//                                            object : LoanClickInterface {
//                                                override fun onLoanClick(position: Int) {
//                                                    startActivity(
//                                                        Intent(
//                                                            mContext,
//                                                            LoanDetailsActivity::class.java
//                                                        ).putExtra(
//                                                            Constants.customerListId,
//                                                            loanListModel.data?.data!![position].customerId
//                                                        ).putExtra(
//                                                            Constants.accountListId,
//                                                            loanListModel.data?.data!![position].id
//                                                        )
//                                                    )
//                                                }
//                                            })
//                                    }
//                                }
//                                loanScreenBinding!!.rvLoanCustomer.adapter = loanCustomerAdapter

                                if (loanListModel.data?.data?.isNotEmpty() == true) {

                                    loanListModel.data!!.data?.let {
                                        datumLoanListModel?.addAll(
                                            it
                                        )
                                    }
                                    loanCustomerAdapter!!.notifyDataSetChanged()

                                } else {
                                    ArrayList<DatumCustomerListV1Model>()
                                }

                                nextPageUrl = loanListModel.data?.nextPageUrl
                                totalPage = loanListModel.data?.lastPage
                                currentPage = loanListModel.data?.currentPage

                                if (datumLoanListModel != null && datumLoanListModel?.isNotEmpty() == true) {
                                    loanScreenBinding?.tvNoRecordFound?.visibility = View.GONE
                                    loanScreenBinding?.rvLoanCustomer?.visibility = View.VISIBLE
                                } else {
                                    loanScreenBinding?.tvNoRecordFound?.visibility = View.VISIBLE
                                    loanScreenBinding?.rvLoanCustomer?.visibility = View.GONE
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

                override fun onFailure(call: Call<LoanListModel?>, throwable: Throwable) {
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

    private fun loanListPageApi() {
        if (nextPageUrl == null) return
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = nextPageUrl?.let { ApiClient.buildService(mContext).loanListNextPageApi(it) }
            call1?.enqueue(object : Callback<LoanListModel?> {
                override fun onResponse(
                    call: Call<LoanListModel?>,
                    response: Response<LoanListModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val loanListModel: LoanListModel? = response.body()
                        if (loanListModel != null) {
                            CommonFunction.showToastSingle(mContext, loanListModel.message, 0)
                            if (loanListModel.status == 200) {
                                if (loanListModel.data?.data?.isNotEmpty() == true) {

                                    loanListModel.data!!.data?.let {
                                        datumLoanListModel?.addAll(
                                            it
                                        )
                                    }
                                    loanCustomerAdapter!!.notifyDataSetChanged()

                                } else {
                                    ArrayList<DatumCustomerListV1Model>()
                                }

                                nextPageUrl = loanListModel.data?.nextPageUrl
                                totalPage = loanListModel.data?.lastPage
                                currentPage = loanListModel.data?.currentPage

                                if (datumLoanListModel != null && datumLoanListModel?.isNotEmpty() == true) {
                                    loanScreenBinding?.tvNoRecordFound?.visibility = View.GONE
                                    loanScreenBinding?.rvLoanCustomer?.visibility = View.VISIBLE
                                } else {
                                    loanScreenBinding?.tvNoRecordFound?.visibility = View.VISIBLE
                                    loanScreenBinding?.rvLoanCustomer?.visibility = View.GONE
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

                override fun onFailure(call: Call<LoanListModel?>, throwable: Throwable) {
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