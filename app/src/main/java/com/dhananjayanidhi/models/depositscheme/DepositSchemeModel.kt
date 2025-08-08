package com.dhananjayanidhi.models.depositscheme

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DepositSchemeModel{
    @SerializedName("data")
    var data: DataDepositSchemeModel? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("success")
    var success: Boolean? = null
}
