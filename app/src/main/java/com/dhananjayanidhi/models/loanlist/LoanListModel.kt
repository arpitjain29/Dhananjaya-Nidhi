package com.dhananjayanidhi.models.loanlist

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoanListModel :CommonModel(){
    @SerializedName("data")
    @Expose
    var data: DataLoanListModel? = null
}
