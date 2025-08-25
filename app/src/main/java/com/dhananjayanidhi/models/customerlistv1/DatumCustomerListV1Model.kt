package com.dhananjayanidhi.models.customerlistv1

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DatumCustomerListV1Model {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("agent_id")
    @Expose
    var agentId: Int? = null

    @SerializedName("customer_id")
    @Expose
    var customerId: String? = null

    @SerializedName("collection_type")
    @Expose
    var collectionType: String? = null

    @SerializedName("collection_amount")
    @Expose
    var collectionAmount: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("account_id")
    @Expose
    var accountId: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("today_collection_status")
    @Expose
    var todayCollectionStatus: String? = null

    @SerializedName("customer_name")
    @Expose
    var customerName: String? = null

    @SerializedName("mobile_number")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("agent_name")
    @Expose
    var agentName: String? = null

    @SerializedName("cutomer_address")
    @Expose
    var cutomerAddress: CutomerAddressCustomerListV1Model? = null

    @SerializedName("account")
    @Expose
    var account: AccountCustomerListV1Model? = null
}
