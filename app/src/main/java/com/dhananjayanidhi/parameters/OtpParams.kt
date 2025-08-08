package com.dhananjayanidhi.parameters

import com.google.gson.annotations.SerializedName

class OtpParams {
    @SerializedName("otp")
    var otp: String? = null
    @SerializedName("mobile")
    var mobile: String? = null
}