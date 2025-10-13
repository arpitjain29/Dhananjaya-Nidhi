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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.CustomerDetailsAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityCustomerDetailsScreenBinding
import com.dhananjayanidhi.databinding.EnterAmountLayoutBinding
import com.dhananjayanidhi.models.customerdetail.CustomerDetailModel
import com.dhananjayanidhi.models.paymentcollection.PaymentCollectionModel
import com.dhananjayanidhi.parameters.CustomerDetailsParams
import com.dhananjayanidhi.parameters.CustomerSearchParams
import com.dhananjayanidhi.parameters.PaymentCollectionParams
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
import com.dhananjayanidhi.databinding.MsgPopupBinding

class CustomerDetailsScreenActivity : BaseActivity() {
    private var customerDetailsScreenBinding: ActivityCustomerDetailsScreenBinding? = null
    private var customerDetailsAdapter: CustomerDetailsAdapter? = null
    private var customerId: String? = null
    private var accountId: String? = null
    private var searching: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        customerDetailsScreenBinding = ActivityCustomerDetailsScreenBinding.inflate(layoutInflater)
        setContentView(customerDetailsScreenBinding!!.root)
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
        searching = intent.getStringExtra(Constants.searchText)
        customerId = intent.getStringExtra(Constants.customerListId)
        accountId = intent.getStringExtra(Constants.accountListId)

        customerDetailsScreenBinding!!.appLayout.ivMenu.visibility = View.GONE
        customerDetailsScreenBinding!!.appLayout.tvTitle.visibility = View.VISIBLE
        customerDetailsScreenBinding!!.appLayout.tvTitle.text = getString(R.string.customer_detail)
        customerDetailsScreenBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        customerDetailsScreenBinding!!.appLayout.ivSearch.visibility = View.GONE

        customerDetailsScreenBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

//        if (customerId == "" && accountId == "") {
//            val customerSearchParams = CustomerSearchParams()
//            customerSearchParams.accountNumber = searching
//            customerSearchApi(customerSearchParams)
//        } else {
            val customerDetailsParams = CustomerDetailsParams()
            customerDetailsParams.customerId = customerId
            customerDetailsParams.accountId = accountId
            customerDetailsApi(customerDetailsParams)
//        }

//        val layoutManager = GridLayoutManager(this, 2)
//        customerDetailsScreenBinding!!.rvCustomerDetail.setLayoutManager(layoutManager)
    }

    private fun customerDetailsApi(customerDetailsParams: CustomerDetailsParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).customerDetailsApi(customerDetailsParams)
            call1?.enqueue(object : Callback<CustomerDetailModel?> {
                override fun onResponse(
                    call: Call<CustomerDetailModel?>,
                    response: Response<CustomerDetailModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val customerDetailsModel: CustomerDetailModel? = response.body()
                        if (customerDetailsModel != null) {
                            CommonFunction.showToastSingle(
                                mContext,
                                customerDetailsModel.message,
                                0
                            )
                            if (customerDetailsModel.status == 200) {
                                if(customerDetailsModel.data != null){
                                    println("details list ====== " + customerDetailsModel.data?.customerName)
                                    customerDetailsScreenBinding!!.tvNameCustomerDetail.text =
                                        customerDetailsModel.data?.customerName
                                    if (customerDetailsModel.data?.todayCollectionStatus == "yes") {
                                        customerDetailsScreenBinding?.ivCheckCustomer?.visibility =
                                            View.VISIBLE
                                    } else {
                                        customerDetailsScreenBinding?.ivCheckCustomer?.visibility =
                                            View.GONE
                                    }
                                    customerDetailsScreenBinding!!.tvAmountCustomerDetails.text =
                                        String.format(
                                            "%s %s",
                                            getString(R.string.rs),
                                            customerDetailsModel.data?.currentMonthCollection
                                        )
                                    customerDetailsScreenBinding!!.tvAddressCustomerDetails.text =
                                        customerDetailsModel.data?.cutomerAddress?.fullAddress
                                    customerDetailsScreenBinding!!.tvMobileNumberCustomerDetails.text =
                                        customerDetailsModel.data?.mobileNumber
                                    customerDetailsScreenBinding!!.tvAccountNumberDetail.text =
                                        String.format(
                                            "%s %s", "A/C: ",
                                            customerDetailsModel.data?.account?.accountNumber
                                        )
                                    customerDetailsScreenBinding?.tvAccountDetails?.text =
                                        String.format(
                                            "%s %s", "A/C Balance: ",
                                            customerDetailsModel.data?.account?.accountBalence
                                        )

                                    customerDetailsScreenBinding!!.fabViewClick.setOnClickListener {

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
                                                    customerDetailsModel.data?.customerId
                                                paymentCollectionParams.accountId =
                                                    customerDetailsModel.data?.accountId
                                                paymentCollectionParams.amount =
                                                    binding.etAmountCustomer.text.toString().trim()
                                                customerAddAmountApi(paymentCollectionParams)
                                            }
                                        }
                                        dialog.show()
                                    }

                                    customerDetailsAdapter = mContext?.let {
                                        customerDetailsModel.data!!
                                            .ddsAccountTransactions?.let { it1 ->
                                                CustomerDetailsAdapter(
                                                    it1, it
                                                )
                                            }
                                    }
                                    customerDetailsScreenBinding!!.rvCustomerDetail.adapter =
                                        customerDetailsAdapter
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

                override fun onFailure(call: Call<CustomerDetailModel?>, throwable: Throwable) {
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

    private fun customerAddAmountApi(paymentCollectionParams: PaymentCollectionParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 =
                ApiClient.buildService(mContext).addCustomerAmountApi(paymentCollectionParams)
            call1?.enqueue(object : Callback<PaymentCollectionModel?> {
                override fun onResponse(
                    call: Call<PaymentCollectionModel?>,
                    response: Response<PaymentCollectionModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val paymentCollectionModel: PaymentCollectionModel? = response.body()
                        if (paymentCollectionModel != null) {
                            if (paymentCollectionModel.status == 200) {
                                CommonFunction.showToastSingle(
                                    mContext,
                                    paymentCollectionModel.message,
                                    0
                                )
                                val customerDetailsParams = CustomerDetailsParams()
                                customerDetailsParams.customerId = customerId
                                customerDetailsParams.accountId = accountId
                                customerDetailsApi(customerDetailsParams)
                                customerDetailsAdapter?.notifyDataSetChanged()
                                startActivity(Intent(mContext,HomeActivity::class.java))
                                finish()
                            } else if (paymentCollectionModel.status == 202) {
                                showDialog(paymentCollectionModel.message)
                            } else {
                                showDialog(paymentCollectionModel.message)
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

                override fun onFailure(call: Call<PaymentCollectionModel?>, throwable: Throwable) {
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

//    private fun customerSearchApi(customerSearchParams: CustomerSearchParams) {
//        if (isConnectingToInternet(mContext!!)) {
//            showProgressDialog()
//            val call1 = ApiClient.buildService(mContext).searchCustomerApi(customerSearchParams)
//            call1?.enqueue(object : Callback<SearchModel?> {
//                override fun onResponse(
//                    call: Call<SearchModel?>,
//                    response: Response<SearchModel?>
//                ) {
//                    hideProgressDialog()
//                    if (response.isSuccessful) {
//                        val searchAccountModel: SearchModel? = response.body()
//                        if (searchAccountModel != null) {
//                            if (searchAccountModel.success == true) {
//                                println("details list ====== " + searchAccountModel.data!!.customerName)
//                                customerDetailsScreenBinding!!.tvNameCustomerDetail.text =
//                                    searchAccountModel.data?.customerName
//                                customerDetailsScreenBinding!!.tvAmountCustomerDetails.text =
//                                    String.format(
//                                        "%s %s",
//                                        getString(R.string.rs),
//                                        searchAccountModel.data?.collectionAmount
//                                    )
//                                customerDetailsScreenBinding!!.tvAddressCustomerDetails.text =
//                                    searchAccountModel.data?.cutomerAddress?.fullAddress
//                                customerDetailsScreenBinding!!.tvMobileNumberCustomerDetails.text =
//                                    searchAccountModel.data?.mobileNumber
//                                customerDetailsScreenBinding!!.tvAccountNumberDetail.text =
//                                    String.format(
//                                        "%s %s", "A/C = ",
//                                        searchAccountModel.data?.account?.accountNumber
//                                    )
//
//                                customerDetailsScreenBinding!!.fabViewClick.setOnClickListener {
//                                    val dialog =
//                                        Dialog(mContext!!, R.style.CustomAlertDialogStyle_space)
//                                    if (dialog.window != null) {
//                                        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
//                                        dialog.window!!.setGravity(Gravity.CENTER)
//                                    }
//                                    if (dialog.window != null) {
//                                        dialog.window!!.setLayout(
//                                            LinearLayout.LayoutParams.MATCH_PARENT,
//                                            LinearLayout.LayoutParams.WRAP_CONTENT
//                                        )
//                                        dialog.window!!.setBackgroundDrawable(
//                                            Color.TRANSPARENT.toDrawable()
//                                        )
//                                    }
//                                    dialog.setCancelable(true)
//                                    val binding: EnterAmountLayoutBinding =
//                                        EnterAmountLayoutBinding.inflate(
//                                            LayoutInflater.from(
//                                                mContext
//                                            ), null, false
//                                        )
//                                    dialog.setContentView(binding.root)
//                                    binding.btnCollect.setOnClickListener {
//                                        if (TextUtils.isEmpty(
//                                                binding.etAmountCustomer.text.toString().trim()
//                                            )
//                                        ) {
//                                            binding.etAmountCustomer.error =
//                                                getString(R.string.please_enter_amount)
//                                        } else {
//                                            dialog.dismiss()
//                                            val paymentCollectionParams =
//                                                PaymentCollectionParams()
//                                            paymentCollectionParams.customerId =
//                                                searchAccountModel.data?.customerId
//                                            paymentCollectionParams.accountId =
//                                                searchAccountModel.data?.accountId
//                                            paymentCollectionParams.amount =
//                                                binding.etAmountCustomer.text.toString().trim()
//                                            customerAddAmountApi(paymentCollectionParams)
//                                        }
//                                    }
//                                    dialog.show()
//                                }
//                                val customerDetailsSearchAdapter = mContext?.let {
//                                    searchAccountModel.data!!
//                                        .ddsAccountTransactions?.let { it1 ->
//                                            CustomerDetailsSearchAdapter(
//                                                it1, it
//                                            )
//                                        }
//                                }
//                                customerDetailsScreenBinding!!.rvCustomerDetail.adapter =
//                                    customerDetailsSearchAdapter
//                            } else {
//                                showDialog(searchAccountModel.message)
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
//                override fun onFailure(call: Call<SearchModel?>, throwable: Throwable) {
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