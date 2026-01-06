package com.dhananjayanidhi.models.membernomineedetails

import com.dhananjayanidhi.models.CommonNewModel
import com.google.gson.annotations.SerializedName

class MemberNomineeDetailsModel : CommonNewModel() {
    @SerializedName("data")
    var data: NomineeDetailsDataModel? = null
}

class NomineeDetailsDataModel {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("customer_id")
    var customerId: Int? = null

    @SerializedName("nominee_id")
    var nomineeId: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("dob")
    var dob: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("relation")
    var relation: String? = null

    @SerializedName("aadhar_number")
    var aadharNumber: String? = null

    @SerializedName("pan_number")
    var panNumber: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}

