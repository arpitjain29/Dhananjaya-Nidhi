package com.dhananjayanidhi.models.customerdetail

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class CutomerAddressCustomerDetailModel {
    @SerializedName("address")
    var address: String? = null

    @SerializedName("address_type")
    var addressType: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("customer_id")
    var customerId: Long? = null

    @SerializedName("full_address")
    var fullAddress: String? = null

    @SerializedName("house_no")
    var houseNo: Any? = null

    @SerializedName("id")
    var id: Long? = null

    @SerializedName("landmark")
    var landmark: String? = null

    @SerializedName("pincode")
    var pincode: String? = null

    @SerializedName("state")
    var state: Any? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("street")
    var street: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}
