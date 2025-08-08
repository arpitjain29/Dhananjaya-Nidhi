package com.dhananjayanidhi.models.paymentcollection

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class PaymentCollectionModel:CommonModel() {
    @SerializedName("data")
    var data: DataPaymentCollectionModel? = null
}
