package com.dhananjayanidhi.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.ActivityLoanEntryBinding
import com.dhananjayanidhi.utils.BaseActivity

class LoanEntryActivity : BaseActivity() {
    private var loanEntryActivity:ActivityLoanEntryBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loanEntryActivity = ActivityLoanEntryBinding.inflate(layoutInflater)
        setContentView(loanEntryActivity!!.root)
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
        loanEntryActivity!!.appLayout.ivMenu.visibility = View.GONE
        loanEntryActivity!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        loanEntryActivity!!.appLayout.ivSearch.visibility = View.GONE
        loanEntryActivity!!.appLayout.tvTitle.text = getString(R.string.loan_entry)
        loanEntryActivity!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}