package com.dhananjayanidhi.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.LoanCustomerAdapter
import com.dhananjayanidhi.databinding.ActivityLoanScreenBinding
import com.dhananjayanidhi.utils.BaseActivity
import com.dhananjayanidhi.utils.interfacef.LoanClickInterface

class LoanScreenActivity : BaseActivity() {
    private var loanScreenBinding: ActivityLoanScreenBinding? = null
    private var loanCustomerAdapter: LoanCustomerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loanScreenBinding = ActivityLoanScreenBinding.inflate(layoutInflater)
        setContentView(loanScreenBinding!!.root)
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
        loanScreenBinding!!.appLayout.ivMenu.visibility = View.GONE
        loanScreenBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        loanScreenBinding!!.appLayout.ivSearch.visibility = View.VISIBLE
        loanScreenBinding!!.appLayout.tvTitle.visibility = View.VISIBLE
        loanScreenBinding!!.appLayout.tvTitle.text = getString(R.string.loan)
        loanScreenBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        loanCustomerAdapter = mContext?.let {
            LoanCustomerAdapter(ArrayList(), it, object : LoanClickInterface {
                override fun onLoanClick(position: Int) {
                    startActivity(Intent(mContext, LoanDetailsActivity::class.java))
                }
            })
        }
        loanScreenBinding!!.rvLoanCustomer.adapter = loanCustomerAdapter
    }
}