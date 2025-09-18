package com.dhananjayanidhi.models.loansearch

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoanSearchModel :CommonModel(){
//    @SerializedName("success")
//    @Expose
//    var success: Int? = null
//
//    @SerializedName("message")
//    @Expose
//    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: DataLoanSearchModel? = null

    @SerializedName("account_type")
    @Expose
    var accountType: String? = null
}
