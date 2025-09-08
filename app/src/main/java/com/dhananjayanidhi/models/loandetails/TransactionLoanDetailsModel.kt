package com.dhananjayanidhi.models.loandetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TransactionLoanDetailsModel {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("agent_id")
    @Expose
    var agentId: Int? = null

    @SerializedName("customer_id")
    @Expose
    var customerId: Int? = null

    @SerializedName("loan_id")
    @Expose
    var loanId: Int? = null

    @SerializedName("opening_balence")
    @Expose
    var openingBalence: String? = null

    @SerializedName("amount")
    @Expose
    var amount: String? = null

    @SerializedName("closing_balence")
    @Expose
    var closingBalence: String? = null

    @SerializedName("deposite_date")
    @Expose
    var depositeDate: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("collection_date")
    @Expose
    var collectionDate: String? = null
}
