package com.dhananjayanidhi.models.loanamount

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class LoanAmountModel:CommonModel() {
    @SerializedName("data")
    var data: DataLoanAmountModel? = null
}
