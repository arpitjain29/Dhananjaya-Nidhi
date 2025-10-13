package com.dhananjayanidhi.models.customerdetail

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DdsAccountTransactionCustomerDetailModel {
    @SerializedName("account_id")
    var accountId: Long? = null

    @SerializedName("agent_id")
    var agentId: Any? = null

    @SerializedName("amount")
    var amount: String? = null

    @SerializedName("comments")
    var comments: Any? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("customer_id")
    var customerId: Long? = null

    @SerializedName("deposit_date")
    var depositDate: String? = null
    @SerializedName("closing_balence")
    var closingBalence: String? = null
    @SerializedName("transaction_date")
    var transactionDate: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}
