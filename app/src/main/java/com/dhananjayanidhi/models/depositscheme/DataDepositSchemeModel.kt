package com.dhananjayanidhi.models.depositscheme

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataDepositSchemeModel {
    @SerializedName("member_fees")
    var memberFees: String? = null

    @SerializedName("schemes")
    var schemes: List<SchemeDepositSchemeModel>? = null
}
