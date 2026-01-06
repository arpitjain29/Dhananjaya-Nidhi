package com.dhananjayanidhi.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dhananjayanidhi.R
import com.dhananjayanidhi.databinding.ActivityLoanEntryBinding
import com.dhananjayanidhi.utils.BaseActivity

class LoanEntryActivity : BaseActivity() {
    private var loanEntryActivity: ActivityLoanEntryBinding? = null

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

        // Add TextWatchers to clear errors when user types
        setupTextWatchers()

        loanEntryActivity!!.btnCollect.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun validateAndSubmit() {
        val name = loanEntryActivity?.etNameLoanEntry?.text.toString().trim()
        val address = loanEntryActivity?.etAddressLoanEntry?.text.toString().trim()
        val loanAmount = loanEntryActivity?.etLoanAmountLoanEntry?.text.toString().trim()
        val duration = loanEntryActivity?.etDurationLoanEntry?.text.toString().trim()
        val mobileNumber = loanEntryActivity?.etMobileNumber?.text.toString().trim()

        // Clear all previous errors
        clearAllErrors()

        var hasError = false

        if (TextUtils.isEmpty(name)) {
            loanEntryActivity?.tilNameLoanEntry?.apply {
                isErrorEnabled = true
                error = getString(R.string.please_enter_name)
            }
            hasError = true
        }
        if (TextUtils.isEmpty(address)) {
            loanEntryActivity?.tilAddressLoanEntry?.apply {
                isErrorEnabled = true
                error = getString(R.string.please_enter_address)
            }
            hasError = true
        }
        if (TextUtils.isEmpty(loanAmount)) {
            loanEntryActivity?.tilLoanAmountLoanEntry?.apply {
                isErrorEnabled = true
                error = getString(R.string.please_enter_loan_amount)
            }
            hasError = true
        }
        if (TextUtils.isEmpty(duration)) {
            loanEntryActivity?.tilDurationLoanEntry?.apply {
                isErrorEnabled = true
                error = getString(R.string.please_enter_duration)
            }
            hasError = true
        }
        if (TextUtils.isEmpty(mobileNumber) || mobileNumber.length != 10) {
            loanEntryActivity?.tilMobileNumberLoanEntry?.apply {
                isErrorEnabled = true
                error = getString(R.string.please_enter_your_mobile_number)
            }
            hasError = true
        }

        if (!hasError) {
            // TODO: Add API call here when available
            // submitLoanEntry(name, address, loanAmount, duration, mobileNumber)
        }
    }

    private fun clearAllErrors() {
        loanEntryActivity?.tilNameLoanEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        loanEntryActivity?.tilAddressLoanEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        loanEntryActivity?.tilLoanAmountLoanEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        loanEntryActivity?.tilDurationLoanEntry?.apply {
            error = null
            isErrorEnabled = false
        }
        loanEntryActivity?.tilMobileNumberLoanEntry?.apply {
            error = null
            isErrorEnabled = false
        }
    }

    private fun setupTextWatchers() {
        // Create a simple TextWatcher that clears error for the associated TextInputLayout
        fun createErrorClearingWatcher(til: com.google.android.material.textfield.TextInputLayout?) =
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    til?.error = null
                    til?.isErrorEnabled = false
                }
            }

        loanEntryActivity?.etNameLoanEntry?.addTextChangedListener(
            createErrorClearingWatcher(loanEntryActivity?.tilNameLoanEntry)
        )
        loanEntryActivity?.etAddressLoanEntry?.addTextChangedListener(
            createErrorClearingWatcher(loanEntryActivity?.tilAddressLoanEntry)
        )
        loanEntryActivity?.etLoanAmountLoanEntry?.addTextChangedListener(
            createErrorClearingWatcher(loanEntryActivity?.tilLoanAmountLoanEntry)
        )
        loanEntryActivity?.etDurationLoanEntry?.addTextChangedListener(
            createErrorClearingWatcher(loanEntryActivity?.tilDurationLoanEntry)
        )
        loanEntryActivity?.etMobileNumber?.addTextChangedListener(
            createErrorClearingWatcher(loanEntryActivity?.tilMobileNumberLoanEntry)
        )
    }
}