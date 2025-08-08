package com.dhananjayanidhi.models.usermodel

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class UserUserLoginModel {
    @SerializedName("aadhar_back_img")
    var aadharBackImg: String? = null

    @SerializedName("aadhar_back_url")
    var aadharBackUrl: String? = null

    @SerializedName("aadhar_front_img")
    var aadharFrontImg: String? = null

    @SerializedName("aadhar_front_url")
    var aadharFrontUrl: String? = null

    @SerializedName("aadhar_number")
    var aadharNumber: String? = null

    @SerializedName("access_token")
    var accessToken: AccessTokenUserLoginModel? = null

    @SerializedName("account_type")
    var accountType: Any? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("city_id")
    var cityId: Long? = null

    @SerializedName("country_code")
    var countryCode: Any? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("default_share_value")
    var defaultShareValue: Any? = null

    @SerializedName("device_token")
    var deviceToken: Any? = null

    @SerializedName("device_type")
    var deviceType: String? = null

    @SerializedName("dob")
    var dob: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("email_token")
    var emailToken: Any? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("full_name")
    var fullName: String? = null

    @SerializedName("gender")
    var gender: Any? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("image_url")
    var imageUrl: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("latitude")
    var latitude: Any? = null

    @SerializedName("longitude")
    var longitude: Any? = null

    @SerializedName("mobile")
    var mobile: String? = null

    @SerializedName("mobile_token")
    var mobileToken: Any? = null

    @SerializedName("mobile_verified_at")
    var mobileVerifiedAt: Any? = null

    @SerializedName("mobile_verify_status")
    var mobileVerifyStatus: Long? = null

    @SerializedName("number_of_share")
    var numberOfShare: Any? = null

    @SerializedName("pan_img")
    var panImg: String? = null

    @SerializedName("pan_number")
    var panNumber: String? = null

    @SerializedName("pan_url")
    var panUrl: String? = null

    @SerializedName("profile_image")
    var profileImage: String? = null

    @SerializedName("profile_image_url")
    var profileImageUrl: String? = null

    @SerializedName("profile_status")
    var profileStatus: String? = null

    @SerializedName("promotors_id")
    var promotorsId: Any? = null

    @SerializedName("role")
    var role: String? = null

    @SerializedName("share_amount")
    var shareAmount: String? = null

    @SerializedName("signature")
    var signature: Any? = null

    @SerializedName("signature_url")
    var signatureUrl: String? = null

    @SerializedName("state_id")
    var stateId: Long? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("wallet")
    var wallet: String? = null
}
