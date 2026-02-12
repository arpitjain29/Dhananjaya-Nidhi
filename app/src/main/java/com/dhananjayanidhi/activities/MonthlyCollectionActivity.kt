package com.dhananjayanidhi.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.MonthlyCollectionCustomerAdapter
import com.dhananjayanidhi.adapter.MonthlyCollectionDateAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityMonthlyCollectionBinding
import com.dhananjayanidhi.models.monthlycollection.MonthlyCollectionModel
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MonthlyCollectionActivity : BaseActivity() {

    private var binding: ActivityMonthlyCollectionBinding? = null

    private var dateWiseAdapter: MonthlyCollectionDateAdapter? = null
    private var customerWiseAdapter: MonthlyCollectionCustomerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMonthlyCollectionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
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

        setupToolbar()
        setupTabs()
        setupRecyclerViews()
        setCurrentMonthHeader()
        loadMonthlyCollection()
    }

    private fun setupToolbar() {
        binding?.appLayout?.ivMenu?.visibility = View.GONE
        binding?.appLayout?.ivBackArrow?.visibility = View.VISIBLE
        binding?.appLayout?.ivFilterIcon?.visibility = View.GONE
        binding?.appLayout?.ivSearch?.visibility = View.GONE
        binding?.appLayout?.tvTitle?.text = getString(R.string.monthly_collection)
        binding?.appLayout?.ivBackArrow?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setCurrentMonthHeader() {
        val formatter = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
        binding?.tvCurrentMonth?.text = formatter.format(Date())
    }

    private fun setupTabs() {
        binding?.tabLayout?.apply {
            removeAllTabs()
            addTab(newTab().setText(getString(R.string.tab_date_wise_collection)), true)
            addTab(newTab().setText(getString(R.string.tab_customer_wise_collection)))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val isDateWise = tab.position == 0
                    binding?.rvDateWise?.visibility = if (isDateWise) View.VISIBLE else View.GONE
                    binding?.rvCustomerWise?.visibility = if (isDateWise) View.GONE else View.VISIBLE
                }

                override fun onTabUnselected(tab: TabLayout.Tab) = Unit
                override fun onTabReselected(tab: TabLayout.Tab) = Unit
            })
        }
    }

    private fun setupRecyclerViews() {
        binding?.rvDateWise?.layoutManager = LinearLayoutManager(this)
        binding?.rvCustomerWise?.layoutManager = LinearLayoutManager(this)
    }

    private fun loadMonthlyCollection() {
        val context = mContext ?: return

        if (!isConnectingToInternet(context)) {
            CommonFunction.showToastSingle(context, getString(R.string.net_connection), 0)
            return
        }

        showProgressDialog()
        val call = ApiClient.buildService(context).monthlyCollectionApi()
        call?.enqueue(object : Callback<MonthlyCollectionModel?> {
            override fun onResponse(
                call: Call<MonthlyCollectionModel?>,
                response: Response<MonthlyCollectionModel?>
            ) {
                hideProgressDialog()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success == true) {
                        val data = body.data
                        val total = data?.totalCollection ?: 0
                        binding?.tvTotalCollection?.text =
                            getString(R.string.rupee_amount_format, total)

                        val dateWise = data?.datewiseCollection.orEmpty()
                        val customerWise = data?.customerwiseCollection.orEmpty()

                        dateWiseAdapter = MonthlyCollectionDateAdapter(dateWise)
                        customerWiseAdapter = MonthlyCollectionCustomerAdapter(customerWise)

                        binding?.rvDateWise?.adapter = dateWiseAdapter
                        binding?.rvCustomerWise?.adapter = customerWiseAdapter

                        val hasAnyData = dateWise.isNotEmpty() || customerWise.isNotEmpty()
                        binding?.tvNoData?.visibility = if (hasAnyData) View.GONE else View.VISIBLE
                    } else {
                        binding?.tvNoData?.visibility = View.VISIBLE
                        val msg = body?.message ?: getString(R.string.error_occurred)
                        CommonFunction.showToastSingle(context, msg, 0)
                    }
                } else {
                    binding?.tvNoData?.visibility = View.VISIBLE
                    CommonFunction.showToastSingle(context, getString(R.string.error_occurred), 0)
                    AppController.instance?.sessionManager?.logoutUser()
                }
            }

            override fun onFailure(call: Call<MonthlyCollectionModel?>, t: Throwable) {
                hideProgressDialog()
                binding?.tvNoData?.visibility = View.VISIBLE
                CommonFunction.showToastSingle(context, getString(R.string.error_occurred), 0)
            }
        })
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}

