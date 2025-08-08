package com.dhananjayanidhi.models.paymentcollection

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataPaymentCollectionModel{
    @SerializedName("account_id")
    var accountId: String? = null

    @SerializedName("agent_id")
    var agentId: String? = null

    @SerializedName("amount")
    var amount: String? = null

    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("deposit_date")
    var depositDate: String? = null
}
