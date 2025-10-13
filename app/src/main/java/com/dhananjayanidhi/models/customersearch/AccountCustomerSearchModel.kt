package com.dhananjayanidhi.models.customersearch

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AccountCustomerSearchModel :Serializable{
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("account_number")
    @Expose
    var accountNumber: String? = null

    @SerializedName("account_type")
    @Expose
    var accountType: Int? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("customer_name")
    @Expose
    var customerName: String? = null

    @SerializedName("maturity_date")
    @Expose
    var maturityDate: String? = null

    @SerializedName("account_type_name")
    @Expose
    var accountTypeName: String? = null

    @SerializedName("agent_name")
    @Expose
    var agentName: String? = null

    @SerializedName("diary_status")
    @Expose
    var diaryStatus: DiaryStatusCustomerSearchModel? = null

    @SerializedName("balence_amount")
    @Expose
    var balenceAmount: String? = null
}
