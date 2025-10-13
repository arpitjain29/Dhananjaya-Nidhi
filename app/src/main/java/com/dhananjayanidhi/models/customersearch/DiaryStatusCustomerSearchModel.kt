package com.dhananjayanidhi.models.customersearch

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DiaryStatusCustomerSearchModel :Serializable{
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("account_id")
    @Expose
    var accountId: Int? = null

    @SerializedName("verify_date")
    @Expose
    var verifyDate: String? = null

    @SerializedName("amount")
    @Expose
    var amount: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}
