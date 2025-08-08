package com.dhananjayanidhi.models.transaction

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class LinkTransactionModel {
    @SerializedName("active")
    var active: Boolean? = null

    @SerializedName("label")
    var label: String? = null

    @SerializedName("url")
    var url: Any? = null
}
