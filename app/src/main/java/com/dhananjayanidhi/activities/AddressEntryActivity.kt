package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityAddressEntryBinding
import com.dhananjayanidhi.models.addressentry.AddressEntryModel
import com.dhananjayanidhi.parameters.AddressEntryParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class AddressEntryActivity : BaseActivity() {
    private var addressEntryActivity: ActivityAddressEntryBinding? = null
    private var addCustomerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        addressEntryActivity = ActivityAddressEntryBinding.inflate(layoutInflater)
        setContentView(addressEntryActivity!!.root)
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
        addressEntryActivity!!.appLayout.ivMenu.visibility = View.GONE
        addressEntryActivity!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        addressEntryActivity!!.appLayout.ivSearch.visibility = View.GONE
        addressEntryActivity!!.appLayout.tvTitle.text = getString(R.string.address_entry)

        addCustomerId = intent.getStringExtra(Constants.customerIdGet)

        addressEntryActivity!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        addressEntryActivity?.cbCopyToAddressEntry?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addressEntryActivity?.etHouseNoPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable(
                        addressEntryActivity?.etHouseNoAddressEntry?.text.toString().trim()
                    )
                addressEntryActivity?.etPermanentAddressEntry?.text = Editable.Factory.getInstance()
                    .newEditable(addressEntryActivity?.etAddressEntry?.text.toString().trim())
                addressEntryActivity?.etLandmarkPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable(
                        addressEntryActivity?.etLandmarkAddressEntry?.text.toString()
                            .trim()
                    )
                addressEntryActivity?.etCityPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable(
                        addressEntryActivity?.etCityAddressEntry?.text.toString().trim()
                    )
                addressEntryActivity?.etPincodePermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable(
                        addressEntryActivity?.etPincodeAddressEntry?.text.toString().trim()
                    )
            } else {
                addressEntryActivity?.etHouseNoPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
                addressEntryActivity?.etPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
                addressEntryActivity?.etLandmarkPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
                addressEntryActivity?.etCityPermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
                addressEntryActivity?.etPincodePermanentAddressEntry?.text =
                    Editable.Factory.getInstance().newEditable("")
            }
        }

        addressEntryActivity!!.btnSubmitAddressEntry.setOnClickListener {
            val addressEntryParams = AddressEntryParams()
            addressEntryParams.customerId = addCustomerId
            addressEntryParams.presentHouseNo =
                addressEntryActivity?.etHouseNoAddressEntry?.text.toString().trim()
            addressEntryParams.presentAddress =
                addressEntryActivity?.etAddressEntry?.text.toString().trim()
            addressEntryParams.presentLandmark =
                addressEntryActivity?.etLandmarkAddressEntry?.text.toString().trim()
            addressEntryParams.presentCity =
                addressEntryActivity?.etCityAddressEntry?.text.toString().trim()
            addressEntryParams.presentPincode =
                addressEntryActivity?.etPincodeAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentHouseNo =
                addressEntryActivity?.etHouseNoPermanentAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentAddress =
                addressEntryActivity?.etPermanentAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentLandmark =
                addressEntryActivity?.etLandmarkPermanentAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentCity =
                addressEntryActivity?.etCityPermanentAddressEntry?.text.toString().trim()
            addressEntryParams.parmanentPincode =
                addressEntryActivity?.etPincodePermanentAddressEntry?.text.toString().trim()

            if (TextUtils.isEmpty(addressEntryParams.presentHouseNo)) {
                addressEntryActivity?.etHouseNoAddressEntry?.error =
                    getString(R.string.please_enter_house_no)
            } else if (TextUtils.isEmpty(addressEntryParams.presentAddress)) {
                addressEntryActivity?.etAddressEntry?.error =
                    getString(R.string.please_enter_address)
            } else if (TextUtils.isEmpty(addressEntryParams.presentLandmark)) {
                addressEntryActivity?.etLandmarkAddressEntry?.error =
                    getString(R.string.please_enter_landmark)
            } else if (TextUtils.isEmpty(addressEntryParams.presentCity)) {
                addressEntryActivity?.etCityAddressEntry?.error =
                    getString(R.string.please_enter_city)
            } else if (TextUtils.isEmpty(addressEntryParams.presentPincode)) {
                addressEntryActivity?.etPincodeAddressEntry?.error =
                    getString(R.string.please_enter_pincode)
            } else {
                addressEntryApi(addressEntryParams)
            }
        }
    }

    private fun addressEntryApi(addressEntryParams: AddressEntryParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).addressInfoApi(addressEntryParams)
            call1?.enqueue(object : Callback<AddressEntryModel?> {
                override fun onResponse(
                    call: Call<AddressEntryModel?>,
                    response: Response<AddressEntryModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val addressEntryModel: AddressEntryModel? = response.body()
                        if (addressEntryModel != null) {
                            if (addressEntryModel.success == true) {
                                startActivity(
                                    Intent(
                                        mContext!!,
                                        KycEntryActivity::class.java
                                    ).putExtra(Constants.customerIdGet, addCustomerId)
                                )
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

                override fun onFailure(call: Call<AddressEntryModel?>, throwable: Throwable) {
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