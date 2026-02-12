package com.dhananjayanidhi.parameters

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class NomineeEntryParams {
    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("nominee_name")
    var nomineeName: String? = null

    @SerializedName("relation")
    var relation: String? = null

    @SerializedName("dob")
    var dob: String? = null

    @SerializedName("aadhar_number")
    var aadharNumber: String? = null
}
