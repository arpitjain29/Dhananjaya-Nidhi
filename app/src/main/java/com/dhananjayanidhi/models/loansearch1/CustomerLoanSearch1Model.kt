package com.dhananjayanidhi.models.loansearch1

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustomerLoanSearch1Model {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("mobile_number")
    @Expose
    var mobileNumber: String? = null

    @SerializedName("customer_name")
    @Expose
    var customerName: String? = null

    @SerializedName("ac_type")
    @Expose
    var acType: String? = null
}
