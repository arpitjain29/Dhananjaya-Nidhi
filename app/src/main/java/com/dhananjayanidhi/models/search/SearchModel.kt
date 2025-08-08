package com.dhananjayanidhi.models.search

import com.dhananjayanidhi.models.CommonModel
import com.dhananjayanidhi.models.customerdetail.DataCustomerDetailModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class SearchModel {
    @SerializedName("data")
    var data: DataCustomerDetailModel? = null
    @SerializedName("success")
    var success: Boolean? = null
    @SerializedName("message")
    var message: String? = null
}
