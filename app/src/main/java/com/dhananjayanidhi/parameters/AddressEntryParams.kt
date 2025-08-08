package com.dhananjayanidhi.parameters

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class AddressEntryParams {
    @SerializedName("customer_id")
    var customerId: String? = null

    @SerializedName("parmanent_address")
    var parmanentAddress: String? = null

    @SerializedName("parmanent_city")
    var parmanentCity: String? = null

    @SerializedName("parmanent_house_no")
    var parmanentHouseNo: String? = null

    @SerializedName("parmanent_landmark")
    var parmanentLandmark: String? = null

    @SerializedName("parmanent_pincode")
    var parmanentPincode: String? = null

    @SerializedName("present_address")
    var presentAddress: String? = null

    @SerializedName("present_city")
    var presentCity: String? = null

    @SerializedName("present_house_no")
    var presentHouseNo: String? = null

    @SerializedName("present_landmark")
    var presentLandmark: String? = null

    @SerializedName("present_pincode")
    var presentPincode: String? = null
}
