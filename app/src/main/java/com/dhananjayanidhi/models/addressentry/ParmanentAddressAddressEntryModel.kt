package com.dhananjayanidhi.models.addressentry

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class ParmanentAddressAddressEntryModel {
    @SerializedName("address")
    var address: String? = null

    @SerializedName("address_type")
    var addressType: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("customer_id")
    var customerId: Long? = null

    @SerializedName("house_no")
    var houseNo: String? = null

    @SerializedName("landmark")
    var landmark: String? = null

    @SerializedName("pincode")
    var pincode: String? = null
}
