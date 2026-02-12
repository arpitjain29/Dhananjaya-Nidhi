package com.dhananjayanidhi.models

import com.google.gson.annotations.SerializedName

open class CommonModel {
    @SerializedName("status")
    var status: Int? = null

    @SerializedName("success")
    var success: Boolean? = null
    @SerializedName("message")
    var message: String? = null
}