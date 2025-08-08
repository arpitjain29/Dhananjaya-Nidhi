package com.dhananjayanidhi.models.usermodel

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class AccessTokenUserLoginModel {
    @SerializedName("token")
    var token: String? = null

    @SerializedName("type")
    var type: String? = null
}
