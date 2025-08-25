package com.dhananjayanidhi.models.customerlistv1

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustomerListV1Model :CommonModel(){
    @SerializedName("data")
    @Expose
    var data: DataCustomerListV1Model? = null
}
