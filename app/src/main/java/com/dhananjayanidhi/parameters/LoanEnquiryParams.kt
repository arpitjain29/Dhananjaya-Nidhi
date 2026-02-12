package com.dhananjayanidhi.parameters

data class LoanEnquiryParams(
    val customer_name: String,
    val address: String,
    val mobile_number: String,
    val loan_amount: String,
    val loan_duration: String,
    val loan_purpose: String
)