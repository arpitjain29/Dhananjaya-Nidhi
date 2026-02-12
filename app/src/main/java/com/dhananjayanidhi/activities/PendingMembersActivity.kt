package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.PendingMembersAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityPendingMembersBinding
import com.dhananjayanidhi.models.memberdraft.MemberDraftListModel
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

class PendingMembersActivity : BaseActivity() {
    private var pendingMembersBinding: ActivityPendingMembersBinding? = null
    private var pendingMembersAdapter: PendingMembersAdapter? = null
    private var allMembersList: List<com.dhananjayanidhi.models.memberdraft.DatumMemberDraftModel> = emptyList()
    private var filteredMembersList: List<com.dhananjayanidhi.models.memberdraft.DatumMemberDraftModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pendingMembersBinding = ActivityPendingMembersBinding.inflate(layoutInflater)
        setContentView(pendingMembersBinding!!.root)
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

        pendingMembersBinding?.appLayout?.ivMenu?.visibility = View.GONE
        pendingMembersBinding?.appLayout?.ivBackArrow?.visibility = View.VISIBLE
        pendingMembersBinding?.appLayout?.ivSearch?.visibility = View.GONE
        pendingMembersBinding?.appLayout?.tvTitle?.visibility = View.VISIBLE
        pendingMembersBinding?.appLayout?.tvTitle?.text = getString(R.string.pending_members)
        pendingMembersBinding?.appLayout?.ivBackArrow?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Setup search functionality
        setupSearch()

        // Load member list
        memberDraftListApi()
    }

    private fun setupSearch() {
        pendingMembersBinding?.ivSearchList?.setOnClickListener {
            performSearch()
        }

        pendingMembersBinding?.etSearchMember?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Real-time search as user types
        pendingMembersBinding?.etSearchMember?.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    performSearch()
                }
            }
        )
    }

    private fun performSearch() {
        val searchQuery = pendingMembersBinding?.etSearchMember?.text.toString().trim().lowercase()
        
        if (TextUtils.isEmpty(searchQuery)) {
            // Show all members if search is empty
            filteredMembersList = allMembersList
        } else {
            // Filter members based on search query
            filteredMembersList = allMembersList.filter { member ->
                val customerName = member.customerName?.lowercase() ?: ""
                val mobileNumber = member.mobileNumber?.lowercase() ?: ""
                val customerId = member.customerId?.lowercase() ?: ""
                val acType = member.acType?.lowercase() ?: ""
                
                customerName.contains(searchQuery) ||
                mobileNumber.contains(searchQuery) ||
                customerId.contains(searchQuery) ||
                acType.contains(searchQuery)
            }
        }
        
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        if (filteredMembersList.isEmpty()) {
            pendingMembersBinding?.tvNoRecordFound?.visibility = View.VISIBLE
            pendingMembersBinding?.rvPendingMembers?.visibility = View.GONE
        } else {
            pendingMembersBinding?.tvNoRecordFound?.visibility = View.GONE
            pendingMembersBinding?.rvPendingMembers?.visibility = View.VISIBLE
            
            // Remove duplicates before setting adapter
            val uniqueFilteredList = filteredMembersList.distinctBy { it.id }
            
            if (pendingMembersAdapter == null) {
                // Create adapter only if it doesn't exist
                 pendingMembersAdapter = PendingMembersAdapter(
                    uniqueFilteredList,
                    mContext!!,
                    object : CustomerClickInterface {
                        override fun onCustomerClick(customerId: String?, accountId: String?) {
                            // Find the member with matching customerId
                            val member = uniqueFilteredList.find { it.customerId == customerId }
                            member?.let {
                                // Navigate to CreateMemberActivity with customer data
                                val intent = Intent(mContext, CreateMemberActivity::class.java)
                                intent.putExtra("customer_id", it.id?.toString() ?: "")
                                intent.putExtra("customer_data", it)
                                startActivity(intent)
                            }
                        }
                    })
                pendingMembersBinding?.rvPendingMembers?.adapter = pendingMembersAdapter
            } else {
                // Update existing adapter with new data
                pendingMembersAdapter?.updateList(uniqueFilteredList)
            }
        }
    }

    private fun memberDraftListApi() {
        if (isConnectingToInternet(mContext!!)) {
            showProgressDialog()
            val call1 = ApiClient.buildService(mContext).memberDraftListApi()
            call1?.enqueue(object : Callback<MemberDraftListModel?> {
                override fun onResponse(
                    call: Call<MemberDraftListModel?>,
                    response: Response<MemberDraftListModel?>
                ) {
                    hideProgressDialog()
                    if (response.isSuccessful) {
                        val memberDraftListModel: MemberDraftListModel? = response.body()
                        if (memberDraftListModel != null) {
                            if (memberDraftListModel.status == true) {
                                val memberData = memberDraftListModel.data
                                if (memberData != null && memberData.isNotEmpty()) {
                                    // Remove duplicates based on id
                                    val uniqueMembers = memberData.distinctBy { it.id }
                                    allMembersList = uniqueMembers
                                    filteredMembersList = uniqueMembers
                                    updateRecyclerView()
                                } else {
                                    pendingMembersBinding?.tvNoRecordFound?.visibility = View.VISIBLE
                                    pendingMembersBinding?.rvPendingMembers?.visibility = View.GONE
                                }
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

                override fun onFailure(call: Call<MemberDraftListModel?>, throwable: Throwable) {
                    hideProgressDialog()
                    throwable.printStackTrace()
                    if (throwable is HttpException) {
                        throwable.printStackTrace()
                    }
                    CommonFunction.showToastSingle(
                        mContext,
                        resources.getString(R.string.net_connection), 0
                    )
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

