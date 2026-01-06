package com.dhananjayanidhi.models.memberdraft

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DatumMemberDraftModel : Serializable {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("prefix")
    var prefix: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("middle_name")
    var middleName: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("dob")
    var dob: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("mobile_number")
    var mobileNumber: String? = null

    @SerializedName("alternate_number")
    var alternateNumber: String? = null

    @SerializedName("customer_type")
    var customerType: String? = null

    @SerializedName("occupation")
    var occupation: String? = null

    @SerializedName("annual_income")
    var annualIncome: String? = null

    @SerializedName("caste")
    var caste: String? = null

    @SerializedName("paper_statement")
    var paperStatement: String? = null

    @SerializedName("share_member")
    var shareMember: String? = null

    @SerializedName("assign_share")
    var assignShare: String? = null

    @SerializedName("entity_type")
    var entityType: String? = null

    @SerializedName("father_name")
    var fatherName: String? = null

    @SerializedName("mother_name")
    var motherName: String? = null

    @SerializedName("maritial_status")
    var maritialStatus: String? = null

    @SerializedName("aadhar_number")
    var aadharNumber: String? = null

    @SerializedName("pan_number")
    var panNumber: String? = null

    @SerializedName("account_type")
    var accountType: String? = null

    @SerializedName("sms_facility")
    var smsFacility: String? = null

    @SerializedName("bank_account_number")
    var bankAccountNumber: String? = null

    @SerializedName("ifsc_code")
    var ifscCode: String? = null

    @SerializedName("bank_name")
    var bankName: String? = null

    @SerializedName("branch_name")
    var branchName: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("image")
    var image: String? = null

    @SerializedName("otp")
    var otp: String? = null

    @SerializedName("promotors_id")
    var promotorsId: String? = null

    @SerializedName("member_fees")
    var memberFees: String? = null

    @SerializedName("member_date")
    var memberDate: String? = null

    @SerializedName("created_byId")
    var createdById: Int? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("customer_name")
    var customerName: String? = null

    @SerializedName("ac_type")
    var acType: String? = null
}

