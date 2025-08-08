package com.dhananjayanidhi.models.kycentry

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataKycEntryModel {
    @SerializedName("aadhar_back_image")
    var aadharBackImage: String? = null

    @SerializedName("aadhar_back_url")
    var aadharBackUrl: String? = null

    @SerializedName("aadhar_front_image")
    var aadharFrontImage: String? = null

    @SerializedName("aadhar_front_url")
    var aadharFrontUrl: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("pan_image")
    var panImage: String? = null

    @SerializedName("pan_url")
    var panUrl: String? = null

    @SerializedName("profile_image")
    var profileImage: String? = null

    @SerializedName("profile_image_url")
    var profileImageUrl: String? = null

    @SerializedName("signature")
    var signature: String? = null

    @SerializedName("signature_url")
    var signatureUrl: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}
