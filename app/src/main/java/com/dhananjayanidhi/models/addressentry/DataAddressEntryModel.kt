package com.dhananjayanidhi.models.addressentry

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataAddressEntryModel {
    @SerializedName("parmanentAddress")
    var parmanentAddress: ParmanentAddressAddressEntryModel? = null

    @SerializedName("presentAdd")
    var presentAdd: PresentAddAddressEntryModel? = null
}
