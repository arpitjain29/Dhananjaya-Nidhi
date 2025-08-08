package com.dhananjayanidhi.models.customeradd

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class CustomerAddModel {
    @SerializedName("data")
    var data: DataCustomerAddModel? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("success")
    var success: Boolean? = null
}
