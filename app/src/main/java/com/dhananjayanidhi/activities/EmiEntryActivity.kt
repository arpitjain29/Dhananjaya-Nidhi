package com.dhananjayanidhi.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.ActivityEmiEntryBinding
import com.dhananjayanidhi.utils.BaseActivity

class EmiEntryActivity : BaseActivity() {
    private var emiEntryBinding: ActivityEmiEntryBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        emiEntryBinding = ActivityEmiEntryBinding.inflate(layoutInflater)
        setContentView(emiEntryBinding!!.root)
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
        emiEntryBinding!!.appLayout.ivMenu.visibility = View.GONE
        emiEntryBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        emiEntryBinding!!.appLayout.ivSearch.visibility = View.GONE
        emiEntryBinding!!.appLayout.tvTitle.text = getString(R.string.emi_entry)
        emiEntryBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}