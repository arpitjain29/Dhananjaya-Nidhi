package com.dhananjayanidhi.models.customerlist

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class AccountCustomerListModel {
    @SerializedName("account_number")
    var accountNumber: String? = null

    @SerializedName("account_type")
    var accountType: Long? = null

    @SerializedName("customer_name")
    var customerName: String? = null

    @SerializedName("id")
    var id: Long? = null
}
