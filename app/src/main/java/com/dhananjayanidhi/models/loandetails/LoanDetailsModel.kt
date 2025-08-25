package com.dhananjayanidhi.models.loandetails

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoanDetailsModel :CommonModel(){
    @SerializedName("data")
    @Expose
    var data: DataLoanDetailsModel? = null
}
