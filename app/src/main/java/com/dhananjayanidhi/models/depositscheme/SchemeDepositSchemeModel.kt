package com.dhananjayanidhi.models.depositscheme

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class SchemeDepositSchemeModel {
    @SerializedName("account_type")
    var accountType: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("days")
    var days: String? = null

    @SerializedName("duration_type")
    var durationType: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("interest")
    var interest: String? = null

    @SerializedName("month_range")
    var monthRange: Any? = null

    @SerializedName("months")
    var months: Long? = null

    @SerializedName("scheme_name")
    var schemeName: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("years")
    var years: Long? = null
}
