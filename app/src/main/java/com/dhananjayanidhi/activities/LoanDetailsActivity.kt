package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.LoanCustomerDetailsAdapter
import com.dhananjayanidhi.databinding.ActivityLoanDetailsBinding
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.interfacef.LoanClickInterface

class LoanDetailsActivity : BaseActivity() {
    private var loanDetailsActivity: ActivityLoanDetailsBinding? = null
    private var loanCustomerDetailsAdapter: LoanCustomerDetailsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loanDetailsActivity = ActivityLoanDetailsBinding.inflate(layoutInflater)
        setContentView(loanDetailsActivity!!.root)
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
        loanDetailsActivity!!.appLayout.ivMenu.visibility = View.GONE
        loanDetailsActivity!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        loanDetailsActivity!!.appLayout.ivSearch.visibility = View.GONE
        loanDetailsActivity!!.appLayout.tvTitle.text = getString(R.string.loan_detail)
        loanDetailsActivity!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        loanCustomerDetailsAdapter =
            mContext?.let {
                LoanCustomerDetailsAdapter(ArrayList(), it,object :LoanClickInterface{
                    override fun onLoanClick(position: Int) {
                        startActivity(Intent(mContext,EmiEntryActivity::class.java))
                    }
                })
            }
        loanDetailsActivity!!.rvLoanDetails.adapter = loanCustomerDetailsAdapter
    }
}