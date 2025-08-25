package com.dhananjayanidhi.models.customerlistv1

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LinkCustomerListV1Model {
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
