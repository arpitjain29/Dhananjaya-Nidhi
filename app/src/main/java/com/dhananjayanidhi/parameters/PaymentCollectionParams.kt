package com.dhananjayanidhi.parameters

import com.google.gson.annotations.SerializedName

class PaymentCollectionParams {
    @SerializedName("customer_id")
    var customerId: String? = null
    @SerializedName("account_id")
    var accountId: String? = null
    @SerializedName("amount")
    var amount: String? = null
}