package com.dhananjayanidhi.models.transaction

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class TransactionModel : CommonModel() {
    @SerializedName("data")
    var data: DataTransactionModel? = null
}
