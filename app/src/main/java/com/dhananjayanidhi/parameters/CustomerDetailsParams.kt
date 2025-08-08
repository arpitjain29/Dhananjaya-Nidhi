package com.dhananjayanidhi.parameters

import com.google.gson.annotations.SerializedName

class CustomerDetailsParams {
    @SerializedName("customer_id")
    var customerId: String? = null
    @SerializedName("account_id")
    var accountId: String? = null
}