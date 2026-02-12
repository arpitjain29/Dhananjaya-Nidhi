package com.dhananjayanidhi.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.TransactionAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityTransactionScreenBinding
import com.dhananjayanidhi.databinding.FilterTransactionLayoutBinding
import com.dhananjayanidhi.models.transaction.TransactionModel
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.Calendar
import androidx.core.graphics.drawable.toDrawable
import com.dhananjayanidhi.utils.interfacef.CustomerClickInterface

class TransactionScreenActivity : BaseActivity() {
    private var transactionScreenBinding : ActivityTransactionScreenBinding? = null
    private var transactionAdapter: TransactionAdapter? = null
    private var selectedDate: String? = null
    private var storedAccountNumber: String = ""
    private var storedDate: String = ""
    private var isFilterActive: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        transactionScreenBinding = ActivityTransactionScreenBinding.inflate(layoutInflater)
        setContentView(transactionScreenBinding!!.root)
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
        transactionScreenBinding!!.appLayout.ivMenu.visibility = View.GONE
        transactionScreenBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        transactionScreenBinding!!.appLayout.clFilterIcon.visibility = View.VISIBLE
        transactionScreenBinding!!.appLayout.ivSearch.visibility = View.GONE
        transactionScreenBinding!!.appLayout.tvTitle.text = getString(R.string.transaction)
        transactionScreenBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Restore filter state on activity creation
        if (isFilterActive) {
            updateFilterIconState(true)
        }

        transactionScreenBinding!!.appLayout.clFilterIcon.setOnClickListener {
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
            val binding: FilterTransactionLayoutBinding =
                FilterTransactionLayoutBinding.inflate(
                    LayoutInflater.from(
                        mContext
                    ), null, false
                )
            dialog.setContentView(binding.root)

            // Restore stored filter values
            if (isFilterActive) {
                binding.etAccountNumberFilter.setText(storedAccountNumber)
                if (storedDate.isNotEmpty()) {
                    // Parse stored date format (yyyy/MM/dd) to display format (dd-MM-yyyy)
                    val dateParts = storedDate.split("/")
                    if (dateParts.size == 3) {
                        binding.etDateFilter.setText("${dateParts[2]}-${dateParts[1]}-${dateParts[0]}")
                        selectedDate = storedDate
                    }
                }
                binding.btnClearFilter.visibility = View.VISIBLE
            } else {
                binding.btnClearFilter.visibility = View.GONE
            }

            binding.etDateFilter.setOnClickListener {
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]

                val datePickerDialog = DatePickerDialog(
                    this, R.style.DatePickerDialogTheme,
                    { _, year, monthOfYear, dayOfMonth ->
                        binding.etDateFilter.setText(
                            dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                        )
                        selectedDate = year.toString() + "/" + (monthOfYear + 1) + "/" + dayOfMonth.toString()
                    },
                    mYear,
                    mMonth,
                    mDay
                )
                datePickerDialog.show()
            }

            binding.btnSubmitFilter.setOnClickListener {
                val accountNumber = binding.etAccountNumberFilter.text.toString().trim()
                val date = selectedDate ?: ""
                
                // Store filter values
                storedAccountNumber = accountNumber
                storedDate = date
                isFilterActive = accountNumber.isNotEmpty() || date.isNotEmpty()
                
                // Update filter icon state
                updateFilterIconState(isFilterActive)
                
                dialog.dismiss()
                transactionListApi(accountNumber, date)
            }

            binding.btnClearFilter.setOnClickListener {
                // Clear stored filter values
                storedAccountNumber = ""
                storedDate = ""
                selectedDate = null
                isFilterActive = false
                
                // Update filter icon state
                updateFilterIconState(false)
                
                // Clear dialog fields
                binding.etAccountNumberFilter.setText("")
                binding.etDateFilter.setText("")
                binding.btnClearFilter.visibility = View.GONE
                
                // Reload data without filter
                dialog.dismiss()
                transactionListApi("", "")
            }
            
            dialog.show()
        }

        transactionScreenBinding!!.etTransactionList.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                transactionAdapter?.filter?.filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })

        transactionListApi("","")
    }

    private fun updateFilterIconState(isActive: Boolean) {
        transactionScreenBinding?.appLayout?.clFilterIcon?.isSelected = isActive
        transactionScreenBinding?.appLayout?.ivFilterIcon?.isSelected = isActive
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
                                                it,mContext!!,object : CustomerClickInterface {
                                                    override fun onCustomerClick(customerId: String?, accountId: String?) {}
                                                })
                                        }
                                    transactionScreenBinding!!.rvTransactionList.adapter = transactionAdapter
                                    transactionScreenBinding!!.rvTransactionList.visibility = View.VISIBLE
                                    transactionScreenBinding!!.tvNoFound.visibility = View.GONE
                                    transactionAdapter?.notifyDataSetChanged()
                                }else{
                                    transactionScreenBinding!!.rvTransactionList.visibility = View.GONE
                                    transactionScreenBinding!!.tvNoFound.visibility = View.VISIBLE
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