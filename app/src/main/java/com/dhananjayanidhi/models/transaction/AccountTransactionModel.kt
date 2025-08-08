package com.dhananjayanidhi.models.transaction

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class AccountTransactionModel {
    @SerializedName("account_balence")
    var accountBalence: String? = null

    @SerializedName("account_interest_rate_variance")
    var accountInterestRateVariance: String? = null

    @SerializedName("account_number")
    var accountNumber: String? = null

    @SerializedName("account_type")
    var accountType: Long? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("customer_name")
    var customerName: String? = null

    @SerializedName("days")
    var days: Long? = null

    @SerializedName("deposit_scheme")
    var depositScheme: Long? = null

    @SerializedName("duration_type")
    var durationType: Any? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("instructions")
    var instructions: Any? = null

    @SerializedName("interest_rate")
    var interestRate: String? = null

    @SerializedName("interest_rate_variance")
    var interestRateVariance: String? = null

    @SerializedName("months")
    var months: Long? = null

    @SerializedName("net_rate")
    var netRate: String? = null

    @SerializedName("recuuring_date")
    var recuuringDate: Any? = null

    @SerializedName("relationship")
    var relationship: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("years")
    var years: Long? = null
}
