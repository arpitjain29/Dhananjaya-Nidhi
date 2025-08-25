package com.dhananjayanidhi.models.loanlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LinkLoanListModel {
    @SerializedName("url")
    @Expose
    var url: Any? = null

    @SerializedName("label")
    @Expose
    var label: String? = null

    @SerializedName("active")
    @Expose
    var active: Boolean? = null
}
