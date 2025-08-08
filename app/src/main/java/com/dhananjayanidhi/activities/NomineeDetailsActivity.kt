package com.dhananjayanidhi.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.ActivityNomineeDetailsBinding
import com.dhananjayanidhi.utils.BaseActivity
import java.util.Calendar

class NomineeDetailsActivity : BaseActivity() {
    private var nomineeDetailsBinding: ActivityNomineeDetailsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        nomineeDetailsBinding = ActivityNomineeDetailsBinding.inflate(layoutInflater)
        setContentView(nomineeDetailsBinding!!.root)
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
        nomineeDetailsBinding!!.appLayout.ivMenu.visibility = View.GONE
        nomineeDetailsBinding!!.appLayout.ivBackArrow.visibility = View.VISIBLE
        nomineeDetailsBinding!!.appLayout.ivSearch.visibility = View.GONE
        nomineeDetailsBinding!!.appLayout.tvTitle.text = getString(R.string.nominee_entry)
        nomineeDetailsBinding!!.appLayout.ivBackArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        nomineeDetailsBinding!!.etDobNomineeEntry.setOnClickListener {
            val c = Calendar.getInstance()
            val mYear: Int = c.get(Calendar.YEAR)
            val mMonth: Int = c.get(Calendar.MONTH)
            val mDay: Int = c.get(Calendar.DAY_OF_MONTH)

            // date picker dialog
            val datePickerDialog = DatePickerDialog(
                this@NomineeDetailsActivity,
                { _, year, monthOfYear, dayOfMonth ->
                    nomineeDetailsBinding!!.etDobNomineeEntry.setText(
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        nomineeDetailsBinding!!.btnSubmitNomineeEntry.setOnClickListener {
            startActivity(Intent(mContext,KycEntryActivity::class.java))
        }
    }
}