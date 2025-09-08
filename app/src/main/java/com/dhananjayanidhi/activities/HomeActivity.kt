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
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityHomeBinding
import com.dhananjayanidhi.databinding.LogoutPopupBinding
import com.dhananjayanidhi.models.CommonModel
import com.dhananjayanidhi.models.dashboard.DashboardModel
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

class HomeActivity : BaseActivity() {
    private var homeBinding: ActivityHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding!!.root)
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

        val actionBarToggle = ActionBarDrawerToggle(
            mContext!!, homeBinding!!.main,
            R.string.nav_open, R.string.nav_close
        )
        homeBinding!!.main.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()

        homeBinding!!.ivUserProfile.setOnClickListener {
            homeBinding!!.main.openDrawer(GravityCompat.START)
        }

        homeBinding!!.tvUserName.text =
            AppController.instance?.sessionManager?.getLoginModel?.user?.fullName

        val headerView = homeBinding?.navView?.getHeaderView(0)
        val usernameTextView = headerView?.findViewById<TextView>(R.id.tvHeaderUserName)
        usernameTextView?.text =
            AppController.instance?.sessionManager?.getLoginModel?.user?.fullName

        homeBinding!!.navView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_home) {
                homeBinding!!.main.closeDrawers()
            }

            if (item.itemId == R.id.nav_transaction) {
                startActivity(Intent(mContext, TransactionScreenActivity::class.java))
            }

            if (item.itemId == R.id.nav_loan) {
                startActivity(Intent(mContext, LoanScreenActivity::class.java))
            }
            if (item.itemId == R.id.nav_create_member) {
                startActivity(Intent(mContext, CustomerEntryActivity::class.java))
            }
            if (item.itemId == R.id.nav_logout) {
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
                val binding: LogoutPopupBinding = LogoutPopupBinding.inflate(
                    LayoutInflater.from(
                        mContext
                    ), null, false
                )
                dialog.setContentView(binding.root)
                binding.tvMessageTextPopup.text =
                    resources.getString(R.string.are_you_sure_you_want_to_logout)
                binding.tvNoTextPopup.setOnClickListener {
                    dialog.dismiss()
                }
                binding.tvYesTextPopup.setOnClickListener {
                    dialog.dismiss()
                    logoutApi()
                }
                dialog.show()
            }

            homeBinding!!.main.closeDrawers()
            true
        }

        homeBinding!!.llCustomer.setOnClickListener {
            startActivity(Intent(mContext, CustomerScreenActivity::class.java))
        }

        homeBinding!!.llLoan.setOnClickListener {
            startActivity(Intent(mContext, LoanScreenActivity::class.java))
        }

        homeBinding!!.llLoanEnquiry.setOnClickListener {
            startActivity(Intent(mContext, LoanEntryActivity::class.java))
        }

        homeBinding?.llTodayCollection?.setOnClickListener {
            startActivity(Intent(mContext!!, CollectionListActivity::class.java))
        }

        homeBinding?.swipeRefreshLayoutHome?.setOnRefreshListener {
            getDashboardApi()
        }

        homeBinding!!.ivSearchList.setOnClickListener {
            if (TextUtils.isEmpty(homeBinding!!.etCustomerAccount.text.toString().trim())) {
                homeBinding!!.etCustomerAccount.error =
                    getString(R.string.please_enter_account_number)
            } else {
                startActivity(
                    Intent(
                        mContext,
                        CustomerDetailsScreenActivity::class.java
                    ).putExtra(
                        Constants.searchText,
                        homeBinding!!.etCustomerAccount.text.toString().trim()
                    )
                        .putExtra(
                            Constants.customerListId,
                            ""
                        ).putExtra(Constants.accountListId, "")
//                        .putExtra(Constants.todayCollection,"")
                )
            }
        }

        homeBinding!!.etCustomerAccount.setOnEditorActionListener(
            TextView.OnEditorActionListener
            { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val intent = Intent(
                        mContext,
                        CustomerDetailsScreenActivity::class.java
                    )
                    intent.putExtra(
                        Constants.customerListId,
                        ""
                    )
                    intent.putExtra(
                        Constants.searchText,
                        homeBinding!!.etCustomerAccount.text.toString().trim()
                    )
                    intent.putExtra(Constants.accountListId, "")
//                    intent.putExtra(Constants.todayCollection,"")
                    startActivity(intent)
                    return@OnEditorActionListener true
                }
                false
            })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (homeBinding!!.main.isDrawerOpen(GravityCompat.START)) {
                    homeBinding!!.main.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })

        getDashboardApi()
    }

//    private fun updateProfileMenu() {
//        val headerView = homeBinding?.navView?.getHeaderView(0)
//        val usernameTextView = headerView?.findViewById<TextView>(R.id.tvHeaderUserName)
//        usernameTextView?.text =
//            AppController.instance?.sessionManager?.getLoginModel?.user?.fullName
//    }

    private fun getDashboardApi() {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).dashboardApi()
            call1?.enqueue(object : Callback<DashboardModel?> {
                override fun onResponse(
                    call: Call<DashboardModel?>,
                    response: Response<DashboardModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val dashboardModel: DashboardModel? = response.body()
                        if (dashboardModel != null) {
                            if (dashboardModel.status == 200) {
                                homeBinding!!.tvTotalTarget.text = String.format(
                                    "%s %s",
                                    getString(R.string.rs), dashboardModel.data?.todayTarget
                                )
                                homeBinding!!.tvTotalCollection.text = String.format(
                                    "%s %s",
                                    getString(R.string.rs), dashboardModel.data?.todayCollection
                                )
                                homeBinding?.tvCustomerDdsCounts?.text =
                                    " " + dashboardModel.data?.customerCount + ")"
                                if (dashboardModel.data?.pendingCollections == "0") {
                                    homeBinding?.tvPendingAmount?.visibility = View.GONE
                                }
                                homeBinding?.tvPendingAmount?.text = String.format(
                                    "%s %s %s",
                                    getString(R.string.pending_amount),
                                    getString(R.string.rs),
                                    dashboardModel.data?.pendingCollections
                                )
                                homeBinding?.tvLoanAccountCount?.text =
                                    dashboardModel.data?.loanAccounts
                            } else {
                                AppController.instance?.sessionManager?.logoutUser()
                            }
                        }
                        homeBinding?.swipeRefreshLayoutHome?.isRefreshing = false
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

                override fun onFailure(call: Call<DashboardModel?>, throwable: Throwable) {
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

    private fun logoutApi() {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).logoutApi()
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