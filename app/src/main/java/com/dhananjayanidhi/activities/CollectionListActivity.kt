package com.dhananjayanidhi.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.TransactionAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityCollectionListBinding
import com.dhananjayanidhi.models.transaction.TransactionModel
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class CollectionListActivity : BaseActivity() {
    private var collectionListActivity: ActivityCollectionListBinding? = null
    private var transactionAdapter: TransactionAdapter? = null
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        collectionListActivity = ActivityCollectionListBinding.inflate(layoutInflater)
        setContentView(collectionListActivity!!.root)
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

        collectionListActivity!!.appLayout.ivMenu.visibility = View.GONE
        collectionListActivity!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        collectionListActivity!!.appLayout.ivFilterIcon.visibility = View.GONE
        collectionListActivity!!.appLayout.ivSearch.visibility = View.GONE
        collectionListActivity!!.appLayout.tvTitle.text = getString(R.string.today_collection)
        collectionListActivity!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        collectionListActivity?.llStartDate?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val mYear = calendar[Calendar.YEAR]
            val mMonth = calendar[Calendar.MONTH]
            val mDay = calendar[Calendar.DAY_OF_MONTH]

            val datePickerDialog = DatePickerDialog(
                this, R.style.DatePickerDialogTheme,
                { _, year, monthOfYear, dayOfMonth ->
                    collectionListActivity?.tvStartDate?.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                    selectedDate = year.toString() + "/" + (monthOfYear + 1) + "/" + dayOfMonth.toString()
                    println("date ---==="+dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    println("date selectedDate ---==="+selectedDate)
                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()
        }

        val simpleDateFormat= SimpleDateFormat("yyyy-MM-dd")
//        val simpleDateFormat= SimpleDateFormat("dd-MM-yyyy HH:MM:SS")
        val currentDT: String = simpleDateFormat.format(Date())
        selectedDate = currentDT
        println("date get ======= "+selectedDate)

        collectionListActivity?.rlGoRecharge?.setOnClickListener {
            transactionListApi("",selectedDate!!)
        }

        transactionListApi("",selectedDate!!)
    }

    private fun transactionListApi(accountNumber:String,date:String) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).transactionApi(accountNumber,date)
            call1?.enqueue(object : Callback<TransactionModel?> {
                override fun onResponse(
                    call: Call<TransactionModel?>,
                    response: Response<TransactionModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val transactionModel: TransactionModel? = response.body()
                        if (transactionModel != null) {
                            if (transactionModel.status == 200) {
                                if (transactionModel.data?.data?.size != 0){
                                    transactionAdapter =
                                        transactionModel.data?.data?.let {
                                            TransactionAdapter(
                                                it,mContext!!,object : CustomerClickInterface{
                                                    override fun onCustomerClick(customerId: String?, accountId: String?) {
                                                        println("customer id ========== $customerId")
                                                        println("account id ========== $accountId")
                                                        startActivity(
                                                            Intent(
                                                                mContext,
                                                                CustomerDetailsScreenActivity::class.java
                                                            ).putExtra(
                                                                Constants.searchText, ""
                                                            ).putExtra(
                                                                    Constants.customerListId, customerId
                                                            ).putExtra(Constants.accountListId, accountId)
//                                                                .putExtra(Constants.todayCollection,"")
                                                        )
                                                    }
                                                })
                                        }
                                    collectionListActivity?.rvCollectionList?.adapter = transactionAdapter
                                    collectionListActivity?.rvCollectionList?.visibility = View.VISIBLE
                                    collectionListActivity?.tvNoData?.visibility = View.GONE
                                    transactionAdapter?.notifyDataSetChanged()
                                }else{
                                    collectionListActivity?.rvCollectionList?.visibility = View.GONE
                                    collectionListActivity?.tvNoData?.visibility = View.VISIBLE
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

                override fun onFailure(call: Call<TransactionModel?>, throwable: Throwable) {
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