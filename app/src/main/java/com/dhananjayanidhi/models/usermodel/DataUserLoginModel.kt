package com.dhananjayanidhi.models.usermodel

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataUserLoginModel {
    @SerializedName("access_token")
    var accessToken: AccessTokenUserLoginModel? = null

    @SerializedName("user")
    var user: UserUserLoginModel? = null
}
