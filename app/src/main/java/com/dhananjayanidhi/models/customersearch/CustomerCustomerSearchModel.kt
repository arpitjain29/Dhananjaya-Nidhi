package com.dhananjayanidhi.models.customersearch

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CustomerCustomerSearchModel :Serializable{
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("customer_id")
    @Expose
    var customerId: String? = null

    @SerializedName("prefix")
    @Expose
    var prefix: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("middle_name")
    @Expose
    var middleName: Any? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("mobile_number")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("account_type")
    @Expose
    var accountType: String? = null

    @SerializedName("customer_name")
    @Expose
    var customerName: String? = null

    @SerializedName("ac_type")
    @Expose
    var acType: String? = null
}
