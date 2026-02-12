package com.dhananjayanidhi.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.MonthlyPayoutAdapter
import com.dhananjayanidhi.apiUtils.ApiClient
import com.dhananjayanidhi.databinding.ActivityMonthlyPayoutBinding
import com.dhananjayanidhi.models.agentpayout.AgentMonthlyPayoutModel
import com.dhananjayanidhi.utils.AppController
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.CommonFunction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonthlyPayoutActivity : BaseActivity() {

    private var binding: ActivityMonthlyPayoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMonthlyPayoutBinding.inflate(layoutInflater)
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
        setupRecyclerView()
        loadMonthlyPayouts()
    }

    private fun setupToolbar() {
        binding?.appLayout?.ivMenu?.visibility = View.GONE
        binding?.appLayout?.ivBackArrow?.visibility = View.VISIBLE
        binding?.appLayout?.ivFilterIcon?.visibility = View.GONE
        binding?.appLayout?.ivSearch?.visibility = View.GONE
        binding?.appLayout?.tvTitle?.text = getString(R.string.monthly_payout)
        binding?.appLayout?.ivBackArrow?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        binding?.rvMonthlyPayout?.layoutManager = LinearLayoutManager(this)
    }

    private fun loadMonthlyPayouts() {
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
        val call = ApiClient.buildService(context).agentMonthlyPayoutSummaryApi()
        call?.enqueue(object : Callback<AgentMonthlyPayoutModel?> {
            override fun onResponse(
                call: Call<AgentMonthlyPayoutModel?>,
                response: Response<AgentMonthlyPayoutModel?>
            ) {
                hideProgressDialog()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success == true) {
                        val list = body.data.orEmpty()
                        if (list.isNotEmpty()) {
                            binding?.rvMonthlyPayout?.visibility = View.VISIBLE
                            binding?.tvNoData?.visibility = View.GONE
                            binding?.rvMonthlyPayout?.adapter = MonthlyPayoutAdapter(list)
                        } else {
                            binding?.rvMonthlyPayout?.visibility = View.GONE
                            binding?.tvNoData?.visibility = View.VISIBLE
                        }
                    } else {
                        binding?.rvMonthlyPayout?.visibility = View.GONE
                        binding?.tvNoData?.visibility = View.VISIBLE
                        val msg = body?.message ?: getString(R.string.error_occurred)
                        CommonFunction.showToastSingle(context, msg, 0)
                    }
                } else {
                    binding?.rvMonthlyPayout?.visibility = View.GONE
                    binding?.tvNoData?.visibility = View.VISIBLE
                    CommonFunction.showToastSingle(
                        context,
                        getString(R.string.error_occurred),
                        0
                    )
                    AppController.instance?.sessionManager?.logoutUser()
                }
            }

            override fun onFailure(call: Call<AgentMonthlyPayoutModel?>, t: Throwable) {
                hideProgressDialog()
                binding?.rvMonthlyPayout?.visibility = View.GONE
                binding?.tvNoData?.visibility = View.VISIBLE
                CommonFunction.showToastSingle(
                    context,
                    getString(R.string.error_occurred),
                    0
                )
            }
        })
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}

