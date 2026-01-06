package com.dhananjayanidhi.models.memberaddressinfo

import com.dhananjayanidhi.models.CommonNewModel
import com.google.gson.annotations.SerializedName

class MemberAddressInfoModel : CommonNewModel() {
    @SerializedName("data")
    var data: DataMemberAddressInfoModel? = null
}

class DataMemberAddressInfoModel {
    @SerializedName("present")
    var present: AddressInfoModel? = null

    @SerializedName("parmanent")
    var parmanent: AddressInfoModel? = null
}

class AddressInfoModel {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("customer_id")
    var customerId: Int? = null

    @SerializedName("address_type")
    var addressType: String? = null

    @SerializedName("house_no")
    var houseNo: String? = null

    @SerializedName("street")
    var street: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("landmark")
    var landmark: String? = null

    @SerializedName("pincode")
    var pincode: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("state")
    var state: String? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null

    @SerializedName("full_address")
    var fullAddress: String? = null
}

