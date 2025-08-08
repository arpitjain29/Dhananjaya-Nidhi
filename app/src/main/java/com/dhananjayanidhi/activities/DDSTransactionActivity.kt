package com.dhananjayanidhi.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.adapter.DdsTransactionAdapter
import com.dhananjayanidhi.databinding.ActivityDdstransactionBinding
import com.dhananjayanidhi.utils.BaseActivity

class DDSTransactionActivity : BaseActivity() {
    private var ddsTransactionActivity : ActivityDdstransactionBinding? = null
    private var ddsTransactionAdapter: DdsTransactionAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ddsTransactionActivity = ActivityDdstransactionBinding.inflate(layoutInflater)
        setContentView(ddsTransactionActivity!!.root)
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

        ddsTransactionActivity!!.appLayout.ivMenu.visibility = View.GONE
        ddsTransactionActivity!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        ddsTransactionActivity!!.appLayout.ivSearch.visibility = View.VISIBLE
        ddsTransactionActivity!!.appLayout.tvTitle.visibility = View.GONE
        ddsTransactionActivity!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        ddsTransactionAdapter = mContext?.let { DdsTransactionAdapter(ArrayList(), it) }
        ddsTransactionActivity!!.rvDdsTransaction.adapter = ddsTransactionAdapter

    }
}