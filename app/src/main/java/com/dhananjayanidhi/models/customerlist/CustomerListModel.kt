package com.dhananjayanidhi.models.customerlist

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class CustomerListModel : CommonModel() {
    @SerializedName("data")
    var data: List<DatumCustomerListModel>? = null
}
