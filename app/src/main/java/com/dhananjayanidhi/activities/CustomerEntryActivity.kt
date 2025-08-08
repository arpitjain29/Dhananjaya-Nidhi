package com.dhananjayanidhi.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityCustomerEntryBinding
import com.dhananjayanidhi.models.customeradd.CustomerAddModel
import com.dhananjayanidhi.parameters.CustomerAddParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.Calendar

class CustomerEntryActivity : BaseActivity() {
    private var customerEntryBinding: ActivityCustomerEntryBinding? = null
    private var selectDateOfBirth: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        customerEntryBinding = ActivityCustomerEntryBinding.inflate(layoutInflater)
        setContentView(customerEntryBinding!!.root)
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

        customerEntryBinding!!.appLayout.ivMenu.visibility = View.GONE
        customerEntryBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        customerEntryBinding!!.appLayout.ivSearch.visibility = View.GONE
        customerEntryBinding!!.appLayout.tvTitle.text = getString(R.string.customer_profile)
        customerEntryBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        customerEntryBinding?.etDobCustomerEntry?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    customerEntryBinding?.etDobCustomerEntry?.setText(dat)
                    selectDateOfBirth = dat
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        customerEntryBinding!!.btnSubmitDdsEntry.setOnClickListener {

            val customerAddParams = CustomerAddParams()
            customerAddParams.firstName =
                customerEntryBinding?.etFirstNameCustomerEntry?.text.toString().trim()
            customerAddParams.lastName =
                customerEntryBinding?.etLastNameCustomerEntry?.text.toString().trim()
            customerAddParams.phoneNumber =
                customerEntryBinding?.etMobileNumberDdsEntry?.text.toString().trim()
            customerAddParams.dob = selectDateOfBirth
            customerAddParams.gender =
                customerEntryBinding?.etGenderCustomerEntry?.text.toString().trim()
            customerAddParams.fatherName =
                customerEntryBinding?.etFatherNameCustomerEntry?.text.toString().trim()
            customerAddParams.motherName =
                customerEntryBinding?.etMotherNameCustomerEntry?.text.toString().trim()
            customerAddParams.annualIncome =
                customerEntryBinding?.etAnnualIncomeEntry?.text.toString().trim()
            customerAddParams.occupation =
                customerEntryBinding?.etOccupationCustomerEntry?.text.toString().trim()
            customerAddParams.caste =
                customerEntryBinding?.etCasteCustomerEntry?.text.toString().trim()

            if (TextUtils.isEmpty(customerAddParams.firstName)) {
                customerEntryBinding?.etFirstNameCustomerEntry?.error =
                    getString(R.string.please_enter_your_first_name)
            } else if (TextUtils.isEmpty(customerAddParams.lastName)) {
                customerEntryBinding?.etLastNameCustomerEntry?.error =
                    getString(R.string.please_enter_your_last_name)
            } else if (TextUtils.isEmpty(customerAddParams.phoneNumber) ||
                customerAddParams.phoneNumber!!.length != 10
            ) {
                customerEntryBinding?.etMobileNumberDdsEntry?.error =
                    getString(R.string.please_enter_your_mobile_number)
            } else if (TextUtils.isEmpty(customerAddParams.dob)) {
                customerEntryBinding?.etDobCustomerEntry?.error =
                    getString(R.string.please_enter_your_dob)
            } else if (TextUtils.isEmpty(customerAddParams.gender)) {
                customerEntryBinding?.etGenderCustomerEntry?.error =
                    getString(R.string.please_enter_your_gender)
            } else if (TextUtils.isEmpty(customerAddParams.fatherName)) {
                customerEntryBinding?.etFatherNameCustomerEntry?.error =
                    getString(R.string.please_enter_your_father_name)
            } else if (TextUtils.isEmpty(customerAddParams.motherName)) {
                customerEntryBinding?.etMotherNameCustomerEntry?.error =
                    getString(R.string.please_enter_your_mother_name)
            } else if (TextUtils.isEmpty(customerAddParams.annualIncome)) {
                customerEntryBinding?.etAnnualIncomeEntry?.error =
                    getString(R.string.please_enter_your_annual_income)
            } else if (TextUtils.isEmpty(customerAddParams.occupation)) {
                customerEntryBinding?.etOccupationCustomerEntry?.error =
                    getString(R.string.please_enter_your_occupation)
            } else if (TextUtils.isEmpty(customerAddParams.caste)) {
                customerEntryBinding?.etCasteCustomerEntry?.error =
                    getString(R.string.please_enter_your_casts)
            } else {
                customerAddApi(customerAddParams)
            }
        }
    }

    private fun customerAddApi(customerAddParams: CustomerAddParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).addCustomerApi(customerAddParams)
            call1?.enqueue(object : Callback<CustomerAddModel?> {
                override fun onResponse(
                    call: Call<CustomerAddModel?>,
                    response: Response<CustomerAddModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val customerAddModel: CustomerAddModel? = response.body()
                        if (customerAddModel != null) {
                            CommonFunction.showToastSingle(mContext, customerAddModel.message, 0)
                            if (customerAddModel.success == true) {
                                startActivity(
                                    Intent(
                                        mContext,
                                        AddressEntryActivity::class.java
                                    ).putExtra(Constants.customerIdGet, customerAddModel.data?.id)
                                )
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

                override fun onFailure(call: Call<CustomerAddModel?>, throwable: Throwable) {
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