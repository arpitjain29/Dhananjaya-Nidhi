package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityLoginBinding
import com.dhananjayanidhi.models.CommonModel
import com.dhananjayanidhi.parameters.SignupParams
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class LoginActivity : BaseActivity() {
    private var viewBindingLogin: ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBindingLogin = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBindingLogin!!.root)
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
        viewBindingLogin!!.btnLogin.setOnClickListener {
            val signupParams = SignupParams()
            signupParams.countryCode = Constants.phoneCode
            signupParams.mobile = viewBindingLogin!!.etMobileNumber.text.toString().trim()

            if (TextUtils.isEmpty(signupParams.mobile) ||
                signupParams.mobile!!.length != 10
            ) {
                viewBindingLogin!!.etMobileNumber.error =
                    getString(R.string.please_enter_your_mobile_number)
            } else {
                loginApi(signupParams)
            }
        }
    }

    private fun loginApi(signupParams: SignupParams) {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).signupUserApi(signupParams)
            call1?.enqueue(object : Callback<CommonModel?> {
                override fun onResponse(
                    call: Call<CommonModel?>,
                    response: Response<CommonModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val loginUser: CommonModel? = response.body()
                        if (loginUser != null) {
                            CommonFunction.showToastSingle(mContext, loginUser.message, 0)
                            if (loginUser.status == 200) {
                                startActivity(
                                    Intent(
                                        mContext,
                                        OtpActivity::class.java
                                    ).putExtra(
                                        Constants.mobileNumber,
                                        viewBindingLogin!!.etMobileNumber.text.toString().trim()
                                    )
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

                override fun onFailure(call: Call<CommonModel?>, throwable: Throwable) {
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