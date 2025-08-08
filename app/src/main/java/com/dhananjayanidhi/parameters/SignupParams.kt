package com.dhananjayanidhi.parameters

import com.google.gson.annotations.SerializedName

class SignupParams {
    @SerializedName("country_code")
    var countryCode: String? = null
    @SerializedName("mobile")
    var mobile: String? = null
}