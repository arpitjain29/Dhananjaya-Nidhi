package com.dhananjayanidhi.models.loanlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DatumLoanListModel {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("agent_id")
    @Expose
    var agentId: Int? = null

    @SerializedName("customer_id")
    @Expose
    var customerId: String? = null

    @SerializedName("loan_type")
    @Expose
    var loanType: String? = null

    @SerializedName("account_number")
    @Expose
    var accountNumber: String? = null

    @SerializedName("principal_amount")
    @Expose
    var principalAmount: String? = null

    @SerializedName("loan_amount")
    @Expose
    var loanAmount: String? = null

    @SerializedName("loan_start_date")
    @Expose
    var loanStartDate: String? = null

    @SerializedName("emi")
    @Expose
    var emi: String? = null

    @SerializedName("total_interest")
    @Expose
    var totalInterest: String? = null

    @SerializedName("outstanding_amount")
    @Expose
    var outstandingAmount: String? = null

    @SerializedName("paid_amount")
    @Expose
    var paidAmount: String? = null

    @SerializedName("today_collection_status")
    @Expose
    var todayCollectionStatus: String? = null

    @SerializedName("customer_name")
    @Expose
    var customerName: String? = null

    @SerializedName("account_type_name")
    @Expose
    var accountTypeName: String? = null

    @SerializedName("agent_name")
    @Expose
    var agentName: String? = null
}
