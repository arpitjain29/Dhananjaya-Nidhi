package com.dhananjayanidhi.models.addressentry

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class AddressEntryModel {
    @SerializedName("data")
    var data: DataAddressEntryModel? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("success")
    var success: Boolean? = null
}
