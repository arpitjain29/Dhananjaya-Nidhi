package com.dhananjayanidhi.parameters

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class CustomerAddParams {
    @SerializedName("annual_income")
    var annualIncome: String? = null

    @SerializedName("caste")
    var caste: String? = null

    @SerializedName("dob")
    var dob: String? = null

    @SerializedName("father_name")
    var fatherName: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("mother_name")
    var motherName: String? = null

    @SerializedName("occupation")
    var occupation: String? = null

    @SerializedName("phone_number")
    var phoneNumber: String? = null
}
