package com.dhananjayanidhi.models.addressentry

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class PresentAddAddressEntryModel {
    @SerializedName("address")
    var address: String? = null

    @SerializedName("address_type")
    var addressType: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("customer_id")
    var customerId: Long? = null

    @SerializedName("full_address")
    var fullAddress: String? = null

    @SerializedName("house_no")
    var houseNo: String? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("landmark")
    var landmark: String? = null

    @SerializedName("pincode")
    var pincode: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}
