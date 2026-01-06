package com.dhananjayanidhi.models.memberdocumentinfo

import com.dhananjayanidhi.models.CommonNewModel
import com.google.gson.annotations.SerializedName

class MemberDocumentInfoModel : CommonNewModel() {
    @SerializedName("data")
    var data: DocumentInfoDataModel? = null
}

class DocumentInfoDataModel {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("customer_id")
    var customerId: Int? = null

    @SerializedName("profile_image")
    var profileImage: String? = null

    @SerializedName("aadhar_front_image")
    var aadharFrontImage: String? = null

    @SerializedName("aadhar_back_image")
    var aadharBackImage: String? = null

    @SerializedName("pan_image")
    var panImage: String? = null

    @SerializedName("signature")
    var signature: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("aadhar_number")
    var aadharNumber: String? = null

    @SerializedName("pan_number")
    var panNumber: String? = null

    @SerializedName("profile_image_url")
    var profileImageUrl: String? = null

    @SerializedName("aadhar_front_url")
    var aadharFrontUrl: String? = null

    @SerializedName("aadhar_back_url")
    var aadharBackUrl: String? = null

    @SerializedName("pan_url")
    var panUrl: String? = null

    @SerializedName("signature_url")
    var signatureUrl: String? = null
}

