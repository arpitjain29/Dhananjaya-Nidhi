package com.dhananjayanidhi.parameters

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class AccountOpenParams {
    @SerializedName("account_number")
    var accountNumber: String? = null

    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("dds_amount")
    var ddsAmount: String? = null

    @SerializedName("deposit_amount")
    var depositAmount: String? = null

    @SerializedName("member_fees")
    var memberFees: String? = null

    @SerializedName("scheme_id")
    var schemeId: String? = null
}
