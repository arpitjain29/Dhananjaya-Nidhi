package com.dhananjayanidhi.parameters

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class KycEntryParams {
    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("aadhar_number")
    var aadharNumber : String? = null

    @SerializedName("pan_number")
    var panNumber : String? = null

    @SerializedName("aadhar_front_image")
    var aadharFrontImage: String? = null

    @SerializedName("aadhar_back_image")
    var aadharBackImage: String? = null

    @SerializedName("pan_image")
    var panImage: String? = null

    @SerializedName("customer_picture")
    var customerPicture: String? = null

    @SerializedName("signature")
    var signature: String? = null
}
