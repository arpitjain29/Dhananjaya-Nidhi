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
import com.dhananjayanidhi.utils.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {
    private var viewBindingLogin: ActivityLoginBinding? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBindingLogin = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBindingLogin?.root ?: return)
        
        // Configure status bar after content is set
        window?.decorView?.post {
            configureStatusBar()
        }
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
        viewBindingLogin?.btnLogin?.setOnClickListener {
            val mobileNumber = viewBindingLogin?.etMobileNumber?.text?.toString()?.trim() ?: ""
            
            if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length != 10) {
                viewBindingLogin?.etMobileNumber?.error =
                    getString(R.string.please_enter_your_mobile_number)
            } else {
                val signupParams = SignupParams().apply {
                    countryCode = Constants.phoneCode
                    this.mobile = mobileNumber
                }
                loginApi(signupParams)
            }
        }
    }

    private fun loginApi(signupParams: SignupParams) {
        val context = mContext ?: return
        
        if (!isConnectingToInternet(context)) {
            CommonFunction.showToastSingle(
                context,
                getString(R.string.net_connection),
                0
            )
            return
        }

        showProgressDialog()
        val call1 = ApiClient.buildService(context).signupUserApi(signupParams)
        call1?.enqueue(object : Callback<CommonModel?> {
            override fun onResponse(
                call: Call<CommonModel?>,
                response: Response<CommonModel?>
            ) {
                hideProgressDialog()
                
                if (response.isSuccessful) {
                    val loginUser = response.body()
                    loginUser?.let { user ->
                        CommonFunction.showToastSingle(context, user.message, 0)
                        if (user.status == 200) {
                            val mobileNumber = viewBindingLogin?.etMobileNumber?.text?.toString()?.trim() ?: ""
                            startActivity(
                                Intent(context, OtpActivity::class.java).putExtra(
                                    Constants.mobileNumber,
                                    mobileNumber
                                )
                            )
                        }
                    }
                } else {
                    ErrorHandler.handleErrorResponse(
                        context,
                        response,
                        getString(R.string.error_occurred)
                    )
                }
            }

            override fun onFailure(call: Call<CommonModel?>, throwable: Throwable) {
                hideProgressDialog()
                ErrorHandler.handleFailure(context, throwable)
            }
        })
    }
    
    override fun onDestroy() {
        viewBindingLogin = null
        super.onDestroy()
    }
}