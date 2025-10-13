package com.dhananjayanidhi.models.loansearch1

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoanSearch1Model :CommonModel(){
    @SerializedName("data")
    @Expose
    var data: List<DatumLoanSearch1Model>? = null
}
