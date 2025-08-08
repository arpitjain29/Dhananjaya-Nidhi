package com.dhananjayanidhi.models.dashboard

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DashboardModel : CommonModel() {
    @SerializedName("data")
    var data: DataDashboardModel? = null
}
