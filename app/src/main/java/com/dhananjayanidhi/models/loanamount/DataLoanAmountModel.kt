package com.dhananjayanidhi.models.loanamount

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataLoanAmountModel{
    @SerializedName("customer_id")
    var customerId: Int? = null

    @SerializedName("loan_id")
    var loanId: Int? = null

    @SerializedName("agent_id")
    var agentId: Int? = null

    @SerializedName("opening_balence")
    var openingBalence: String? = null

    @SerializedName("amount")
    var amount: Int? = null

    @SerializedName("closing_balence")
    var closingBalence: Int? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("deposite_date")
    var depositeDate: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("collection_date")
    var collectionDate: String? = null
}
