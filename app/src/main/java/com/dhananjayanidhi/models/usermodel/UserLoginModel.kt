package com.dhananjayanidhi.models.usermodel

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class UserLoginModel : CommonModel() {
    @SerializedName("data")
    var data: DataUserLoginModel? = null
}
