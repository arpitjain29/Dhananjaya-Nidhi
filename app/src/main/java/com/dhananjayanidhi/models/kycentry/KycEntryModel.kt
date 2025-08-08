package com.dhananjayanidhi.models.kycentry

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class KycEntryModel {
    @SerializedName("data")
    var data: DataKycEntryModel? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("success")
    var success: Boolean? = null
}
