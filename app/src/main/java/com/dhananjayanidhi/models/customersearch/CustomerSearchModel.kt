package com.dhananjayanidhi.models.customersearch

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustomerSearchModel :CommonModel(){
    @SerializedName("data")
    @Expose
    var data: List<DatumCustomerSearchModel>? = null
}
