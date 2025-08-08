package com.dhananjayanidhi.models.customeradd

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataCustomerAddModel {
    @SerializedName("ac_type")
    var acType: String? = null

    @SerializedName("account_type")
    var accountType: String? = null

    @SerializedName("annual_income")
    var annualIncome: String? = null

    @SerializedName("caste")
    var caste: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("created_byId")
    var createdById: String? = null

    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("customer_name")
    var customerName: String? = null

    @SerializedName("dob")
    var dob: String? = null

    @SerializedName("father_name")
    var fatherName: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("member_date")
    var memberDate: String? = null

    @SerializedName("mobile_number")
    var mobileNumber: String? = null

    @SerializedName("mother_name")
    var motherName: String? = null

    @SerializedName("occupation")
    var occupation: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}
