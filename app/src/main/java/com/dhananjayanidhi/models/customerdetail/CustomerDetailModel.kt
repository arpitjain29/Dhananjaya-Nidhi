package com.dhananjayanidhi.models.customerdetail

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class CustomerDetailModel :CommonModel(){
    @SerializedName("data")
    var data: DataCustomerDetailModel? = null
}
