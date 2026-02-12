package com.dhananjayanidhi.models.transaction

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DatumTransactionModel {
    @SerializedName("account")
    var account: AccountTransactionModel? = null

    @SerializedName("account_id")
    var accountId: String? = null

    @SerializedName("agent_id")
    var agentId: Long? = null

    @SerializedName("amount")
    var amount: String? = null

    @SerializedName("comments")
    var comments: Any? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("deposit_date")
    var depositDate: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}
