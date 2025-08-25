package com.dhananjayanidhi.models.customerlistv1

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CutomerAddressCustomerListV1Model {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("customer_id")
    @Expose
    var customerId: Int? = null

    @SerializedName("address_type")
    @Expose
    var addressType: String? = null

    @SerializedName("house_no")
    @Expose
    var houseNo: Any? = null

    @SerializedName("street")
    @Expose
    var street: Any? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("landmark")
    @Expose
    var landmark: Any? = null

    @SerializedName("pincode")
    @Expose
    var pincode: String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("state")
    @Expose
    var state: Any? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("full_address")
    @Expose
    var fullAddress: String? = null
}
