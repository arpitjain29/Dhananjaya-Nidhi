package com.dhananjayanidhi.models.customerdetail

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataCustomerDetailModel {
    @SerializedName("account")
    var account: AccountCustomerDetailModel? = null

    @SerializedName("account_id")
    var accountId: String? = null

    @SerializedName("agent_id")
    var agentId: String? = null

    @SerializedName("agent_name")
    var agentName: String? = null
    @SerializedName("current_month_collection")
    var currentMonthCollection: String? = null

    @SerializedName("collection_amount")
    var collectionAmount: String? = null

    @SerializedName("collection_type")
    var collectionType: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("today_collection_status")
    var todayCollectionStatus: String? = null

    @SerializedName("customer")
    var customer: CustomerCustomerDetailModel? = null

    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("customer_name")
    var customerName: String? = null

    @SerializedName("cutomer_address")
    var cutomerAddress: CutomerAddressCustomerDetailModel? = null

    @SerializedName("dds_account_transactions")
    var ddsAccountTransactions: List<DdsAccountTransactionCustomerDetailModel>? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("mobile_number")
    var mobileNumber: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}
