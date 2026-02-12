package com.dhananjayanidhi.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.dhananjayanidhi.models.CommonModel
import com.dhananjayanidhi.models.dashboard.DashboardModel
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.dhananjayanidhi.utils.Constants
import com.dhananjayanidhi.utils.MemberFlowManager
import com.dhananjayanidhi.utils.DialogHelper
import com.dhananjayanidhi.utils.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.graphics.drawable.toDrawable
import com.dhananjayanidhi.models.loansearch.LoanSearchModel
import com.dhananjayanidhi.models.search.SearchModel
import com.dhananjayanidhi.parameters.CustomerSearchParams
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.JsonObject

class HomeActivity : BaseActivity() {
    private var homeBinding: ActivityHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding?.root ?: return)

        // Configure status bar after content is set
        window?.decorView?.post {
            configureStatusBar()
        }
        getDashboardApi()
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

        mContext?.let { context ->
            val actionBarToggle = ActionBarDrawerToggle(
                context,
                homeBinding?.main ?: return@let,
                R.string.nav_open,
                R.string.nav_close
            )
            homeBinding?.main?.addDrawerListener(actionBarToggle)
            actionBarToggle.syncState()
        }

        homeBinding?.ivDrawerMenu?.setOnClickListener {
            if (homeBinding?.main?.isDrawerOpen(GravityCompat.START) == true) {
                homeBinding?.main?.closeDrawer(GravityCompat.START)
            } else {
                homeBinding?.main?.openDrawer(GravityCompat.START)
            }
        }

//        homeBinding?.ivUserProfile?.setOnClickListener {
//            homeBinding?.main?.openDrawer(GravityCompat.START)
//        }

        val userName = AppController.instance?.sessionManager?.getLoginModel?.user?.fullName ?: ""
        val profileImage =
            AppController.instance?.sessionManager?.getLoginModel?.user?.profileImageUrl ?: ""
        homeBinding?.tvUserName?.text = userName
        CommonFunction.loadImageViaGlide(
            mContext,
            profileImage,
            homeBinding?.ivUserProfile,
            R.drawable.app_icon_logo
        )
        val headerView = homeBinding?.navView?.getHeaderView(0)
        val usernameTextView = headerView?.findViewById<TextView>(R.id.tvHeaderUserName)
        val ivHeaderUserImage : ShapeableImageView? = headerView?.findViewById(R.id.ivHeaderUserImage)
        usernameTextView?.text = userName
        CommonFunction.loadImageViaGlide(
            mContext,
            profileImage,
             ivHeaderUserImage,
            R.drawable.app_icon_logo
        )

        homeBinding?.navView?.setNavigationItemSelectedListener { item ->
            val context = mContext ?: return@setNavigationItemSelectedListener false

            when (item.itemId) {
                R.id.nav_home -> {
                    homeBinding?.main?.closeDrawers()
                }

                R.id.nav_transaction -> {
                    startActivity(Intent(context, TransactionScreenActivity::class.java))
                }

                R.id.nav_loan -> {
                    startActivity(Intent(context, LoanScreenActivity::class.java))
                }

                R.id.nav_create_member -> {
                    startMemberCreationFlow(context)
                }

                R.id.nav_customers -> {
                    mContext?.let { context ->
                        startActivity(Intent(context, CustomerScreenActivity::class.java))
                    }
                }

                R.id.nav_pending_members -> {
                    startActivity(Intent(context, PendingMembersActivity::class.java))
                }

                R.id.nav_monthly_payout -> {
                    startActivity(Intent(context, MonthlyPayoutActivity::class.java))
                }

                R.id.nav_monthly_collection -> {
                    startActivity(Intent(context, MonthlyCollectionActivity::class.java))
                }
            }
            if (item.itemId == R.id.nav_logout) {
                mContext?.let { context ->
                    DialogHelper.showConfirmationDialog(
                        activity = context,
                        message = getString(R.string.are_you_sure_you_want_to_logout),
                        positiveButtonText = getString(R.string.logout),
                        negativeButtonText = getString(R.string.cancel),
                        onPositiveClick = { logoutApi() }
                    )
                }
            }

            homeBinding?.main?.closeDrawers()
            true
        }

        homeBinding?.llCustomer?.setOnClickListener {
            mContext?.let { context ->
                startActivity(Intent(context, CustomerScreenActivity::class.java))
            }
        }

        homeBinding?.llLoan?.setOnClickListener {
            mContext?.let { context ->
                startActivity(Intent(context, LoanScreenActivity::class.java))
            }
        }

        homeBinding?.llLoanEnquiry?.setOnClickListener {
            mContext?.let { context ->
                startActivity(Intent(context, LoanEntryActivity::class.java))
            }
        }

        homeBinding?.llTodayCollection?.setOnClickListener {
            mContext?.let { context ->
                startActivity(Intent(context, CollectionListActivity::class.java))
            }
        }

        homeBinding?.swipeRefreshLayoutHome?.setOnRefreshListener {
            getDashboardApi()
        }
        homeBinding?.ivSearchList?.setOnClickListener {
            val accountNumber = homeBinding?.etCustomerAccount?.text?.toString()?.trim() ?: ""
            if (TextUtils.isEmpty(accountNumber)) {
                homeBinding?.etCustomerAccount?.error =
                    getString(R.string.please_enter_account_number)
            } else {
                val customerSearchParams = CustomerSearchParams().apply {
                    this.accountNumber = accountNumber
                }
                searchApi(customerSearchParams)
            }
        }

        homeBinding?.etCustomerAccount?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = homeBinding?.etCustomerAccount?.text?.toString()?.trim() ?: ""
                mContext?.let { context ->
                    startActivity(
                        Intent(context, CustomerDetailsScreenActivity::class.java).apply {
                            putExtra(Constants.customerListId, "")
                            putExtra(Constants.searchText, searchText)
                            putExtra(Constants.accountListId, "")
                        }
                    )
                }
                true
            } else {
                false
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (homeBinding?.main?.isDrawerOpen(GravityCompat.START) == true) {
                    homeBinding?.main?.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }
    
    /**
     * Start member creation flow with resume logic
     * If a flow is in progress, redirects to the next pending step
     * Otherwise, starts a new flow from CustomerEntryActivity
     */
    private fun startMemberCreationFlow(context: Context) {
        if (MemberFlowManager.isFlowInProgress(context)) {
            // Flow is in progress - resume from last completed step
            val nextStep = MemberFlowManager.getNextPendingStep(context)
            val customerId = MemberFlowManager.getCustomerId(context)

            // Navigate to CreateMemberActivity which will handle step navigation
            startActivity(Intent(context, CreateMemberActivity::class.java))
        } else {
            // No flow in progress - start new flow
            startActivity(Intent(context, CreateMemberActivity::class.java))
        }


    }

//    private fun updateProfileMenu() {
//        val headerView = homeBinding?.navView?.getHeaderView(0)
//        val usernameTextView = headerView?.findViewById<TextView>(R.id.tvHeaderUserName)
//        usernameTextView?.text =
//            AppController.instance?.sessionManager?.getLoginModel?.user?.fullName
//    }

    private fun getDashboardApi() {
        val context = mContext ?: return

        if (!isConnectingToInternet(context)) {
            CommonFunction.showToastSingle(
                context,
                getString(R.string.net_connection),
                0
            )
            homeBinding?.swipeRefreshLayoutHome?.isRefreshing = false
            return
        }

        showProgressDialog()
        val call1 = ApiClient.buildService(context).dashboardApi()
        call1?.enqueue(object : Callback<DashboardModel?> {
            override fun onResponse(
                call: Call<DashboardModel?>,
                response: Response<DashboardModel?>
            ) {
                hideProgressDialog()
                homeBinding?.swipeRefreshLayoutHome?.isRefreshing = false

                if (response.isSuccessful) {
                    val dashboardModel = response.body()
                    if (dashboardModel != null) {
                        if (dashboardModel.status == 200) {
                            dashboardModel.data?.let { data ->
                                homeBinding?.tvTotalTarget?.text = String.format(
                                    "%s %s",
                                    getString(R.string.rs),
                                    data.todayTarget ?: "0"
                                )
                                homeBinding?.tvTotalCollection?.text = String.format(
                                    "%s %s",
                                    getString(R.string.rs),
                                    data.todayCollection ?: "0"
                                )
                                homeBinding?.tvCustomerDdsCounts?.text = data.customerCount ?: "0"

                                val pendingCollections = data.pendingCollections ?: "0"
                                if (pendingCollections == "0") {
                                    homeBinding?.tvPendingAmount?.visibility = View.GONE
                                } else {
                                    homeBinding?.tvPendingAmount?.visibility = View.VISIBLE
                                    homeBinding?.tvPendingAmount?.text = String.format(
                                        "%s %s %s",
                                        getString(R.string.pending_amount),
                                        getString(R.string.rs),
                                        pendingCollections
                                    )
                                }
                                homeBinding?.tvLoanAccountCount?.text = data.loanAccounts ?: "0"
                            }
                        } else {
                            AppController.instance?.sessionManager?.logoutUser()
                        }
                    }
                } else {
                    ErrorHandler.handleErrorResponse(
                        context,
                        response,
                        getString(R.string.error_occurred)
                    ) {
                        AppController.instance?.sessionManager?.logoutUser()
                    }
                }
            }

            override fun onFailure(call: Call<DashboardModel?>, throwable: Throwable) {
                hideProgressDialog()
                homeBinding?.swipeRefreshLayoutHome?.isRefreshing = false
                ErrorHandler.handleFailure(context, throwable)
            }
        })
    }

    private fun searchApi(customerSearchParams: CustomerSearchParams) {
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
        val call1 = ApiClient.buildService(context).searchCustomerV1Api(customerSearchParams)
        call1.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                hideProgressDialog()

                if (response.isSuccessful) {
                    val jsonObj = response.body()
                    try {
                        val status = jsonObj?.get("status")?.asInt ?: 0
                        val message = jsonObj?.get("message")?.asString ?: ""

                        if (status == 200) {
                            homeBinding?.etCustomerAccount?.text =
                                Editable.Factory.getInstance().newEditable("")
                            val accountType = jsonObj?.get("account_type")?.asString ?: ""

                            when (accountType) {
                                "loan" -> {
                                    val loanSearchModel =
                                        Gson().fromJson(jsonObj, LoanSearchModel::class.java)
                                    startActivity(
                                        Intent(context, LoanDetailsActivity::class.java).apply {
                                            putExtra(
                                                Constants.customerListId,
                                                loanSearchModel.data?.customerId ?: ""
                                            )
                                            putExtra(
                                                Constants.accountListId,
                                                loanSearchModel.data?.id ?: ""
                                            )
                                        }
                                    )
                                }

                                "dds" -> {
                                    val searchModel =
                                        Gson().fromJson(jsonObj, SearchModel::class.java)
                                    startActivity(
                                        Intent(
                                            context,
                                            CustomerDetailsScreenActivity::class.java
                                        ).apply {
                                            putExtra(
                                                Constants.customerListId,
                                                searchModel.data?.customerId ?: ""
                                            )
                                            putExtra(
                                                Constants.accountListId,
                                                searchModel.data?.accountId ?: ""
                                            )
                                        }
                                    )
                                }
                            }
                        } else {
                            CommonFunction.showToastSingle(context, message, 0)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        ErrorHandler.handleErrorResponse(
                            context,
                            null,
                            getString(R.string.error_occurred)
                        )
                    }
                } else {
                    ErrorHandler.handleErrorResponse(
                        context,
                        response,
                        getString(R.string.error_occurred)
                    ) {
                        AppController.instance?.sessionManager?.logoutUser()
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, throwable: Throwable) {
                hideProgressDialog()
                ErrorHandler.handleFailure(context, throwable)
            }
        })
    }

    private fun logoutApi() {
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
        val call1 = ApiClient.buildService(context).logoutApi()
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
                            AppController.instance?.sessionManager?.logoutUser()
                        }
                    }
                } else {
                    ErrorHandler.handleErrorResponse(
                        context,
                        response,
                        getString(R.string.error_occurred)
                    ) {
                        AppController.instance?.sessionManager?.logoutUser()
                    }
                }
            }

            override fun onFailure(call: Call<CommonModel?>, throwable: Throwable) {
                hideProgressDialog()
                ErrorHandler.handleFailure(context, throwable)
            }
        })
    }

    override fun onDestroy() {
        homeBinding = null
        super.onDestroy()
    }
}