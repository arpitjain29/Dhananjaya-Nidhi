package com.dhananjayanidhi.models.customerlist

import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Suppress("unused")
class DatumCustomerListModel :Serializable{
    @SerializedName("account")
    var account: AccountCustomerListModel? = null

    @SerializedName("account_id")
    var accountId: String? = null

    @SerializedName("agent_id")
    var agentId: String? = null

    @SerializedName("agent_name")
    var agentName: String? = null

    @SerializedName("collection_amount")
    var collectionAmount: String? = null

    @SerializedName("collection_type")
    var collectionType: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("today_collection_status")
    var todayCollectionStatus: String? = null

    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("customer_name")
    var customerName: String? = null

    @SerializedName("cutomer_address")
    var cutomerAddress: CutomerAddressCustomerListModel? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("mobile_number")
    var mobileNumber: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}
