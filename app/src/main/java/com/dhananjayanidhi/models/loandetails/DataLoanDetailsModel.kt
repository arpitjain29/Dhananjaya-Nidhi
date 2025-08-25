package com.dhananjayanidhi.models.loandetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataLoanDetailsModel {
    @SerializedName("account")
    @Expose
    var account: AccountLoanDetailsModel? = null

    @SerializedName("transactions")
    @Expose
    var transactions: List<Any>? = null
}
