package com.dhananjayanidhi.models.accountopen

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class AccountOpenModel {
    @SerializedName("data")
    var data: DataAccountOpenModel? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("success")
    var success: Boolean? = null
}
