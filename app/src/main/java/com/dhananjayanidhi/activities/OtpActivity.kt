package com.dhananjayanidhi.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityOtpBinding
import com.dhananjayanidhi.models.usermodel.UserLoginModel
import com.dhananjayanidhi.parameters.OtpParams
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class OtpActivity : BaseActivity() {
    private var otpBinding: ActivityOtpBinding? = null
    private var mobile: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        otpBinding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(otpBinding!!.root)
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
        mobile = intent.getStringExtra(Constants.mobileNumber)
        otpBinding!!.tvMobileNumber.text = mobile
        otpBinding!!.btnOtp.setOnClickListener {
            val otpParams = OtpParams()
            otpParams.mobile = mobile
            otpParams.otp = otpBinding!!.etOtp1.text.toString().trim() + otpBinding!!
                .etOtp2.text.toString().trim() + otpBinding!!.etOtp3.text.toString()
                .trim() + otpBinding!!.etOtp4.text.toString().trim()
            if (TextUtils.isEmpty(otpParams.otp)) {
                Toast.makeText(this, getString(R.string.please_enter_your_otp),
                    Toast.LENGTH_SHORT).show()
            } else {
                otpApi(otpParams)
//                startActivity(Intent(mContext, HomeActivity::class.java))
            }
        }

        otpBinding!!.etOtp1.addTextChangedListener(
            GenericTextWatcher(
                otpBinding!!.etOtp1,
                otpBinding!!.etOtp2
            )
        )
        otpBinding!!.etOtp2.addTextChangedListener(
            GenericTextWatcher(
                otpBinding!!.etOtp2,
                otpBinding!!.etOtp3
            )
        )
        otpBinding!!.etOtp3.addTextChangedListener(
            GenericTextWatcher(
                otpBinding!!.etOtp3,
                otpBinding!!.etOtp4
            )
        )
        otpBinding!!.etOtp4.addTextChangedListener(
            GenericTextWatcher(
                otpBinding!!.etOtp4,
                null
            )
        )

        otpBinding!!.etOtp1.setOnKeyListener(GenericKeyEvent(otpBinding!!.etOtp1, null))
        otpBinding!!.etOtp2.setOnKeyListener(
            GenericKeyEvent(
                otpBinding!!.etOtp2,
                otpBinding!!.etOtp1
            )
        )
        otpBinding!!.etOtp3.setOnKeyListener(
            GenericKeyEvent(
                otpBinding!!.etOtp3,
                otpBinding!!.etOtp2
            )
        )
        otpBinding!!.etOtp4.setOnKeyListener(
            GenericKeyEvent(
                otpBinding!!.etOtp4,
                otpBinding!!.etOtp3
            )
        )
    }

    class GenericKeyEvent internal constructor(
        private val currentView: EditText,
        private val previousView: EditText?
    ) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL
                && currentView.id != R.id.etOtp1 && currentView.text.isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }
    }

    class GenericTextWatcher internal constructor
        (private val currentView: View, private val nextView: View?) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (currentView.id) {
                R.id.etOtp1 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.etOtp2 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.etOtp3 -> if (text.length == 1) nextView!!.requestFocus()
                //You can use EditText4 same as above to hide the keyboard
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
        }

        override fun onTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
        }
    }

    private fun otpApi(otpParams: OtpParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).otpApi(otpParams)
            call1?.enqueue(object : Callback<UserLoginModel?> {
                override fun onResponse(
                    call: Call<UserLoginModel?>,
                    response: Response<UserLoginModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val loginUser: UserLoginModel? = response.body()
                        if (loginUser != null) {
                            CommonFunction.showToastSingle(mContext, loginUser.message, 0)
                            if (loginUser.status == 200) {
                                AppController.instance?.sessionManager?.getLoginModel = loginUser.data
                                AppController.instance?.sessionManager?.setLoginSession(1)
                                AppController.instance?.sessionManager?.checkLogin()
//                                startActivity(Intent(mContext, HomeActivity::class.java))
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
                            } catch (e: Exception) {
                                e.printStackTrace()
                                CommonFunction.showToastSingle(
                                    mContext,
                                    "An error occurred. Please try again.",
                                    0
                                )
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<UserLoginModel?>, throwable: Throwable) {
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