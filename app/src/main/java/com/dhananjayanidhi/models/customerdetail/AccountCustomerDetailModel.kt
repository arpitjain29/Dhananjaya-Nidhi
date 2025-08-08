package com.dhananjayanidhi.models.customerdetail

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class AccountCustomerDetailModel {
    @SerializedName("account_number")
    var accountNumber: String? = null

    @SerializedName("account_type")
    var accountType: Long? = null

    @SerializedName("customer_name")
    var customerName: String? = null

    @SerializedName("account_balence")
    var accountBalence: String? = null

    @SerializedName("id")
    var id: Long? = null
}
