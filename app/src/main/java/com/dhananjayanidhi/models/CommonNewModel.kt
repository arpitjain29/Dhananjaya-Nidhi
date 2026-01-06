package com.dhananjayanidhi.models

import com.google.gson.annotations.SerializedName

open class CommonNewModel {
    @SerializedName("success")
    var status: Boolean? = null
    @SerializedName("message")
    var message: String? = null
}