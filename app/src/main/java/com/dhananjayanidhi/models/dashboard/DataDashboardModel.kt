package com.dhananjayanidhi.models.dashboard

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataDashboardModel {
    @SerializedName("todayCollection")
    var todayCollection: String? = null

    @SerializedName("todayTarget")
    var todayTarget: String? = null
    @SerializedName("customerCount")
    var customerCount: String? = null
    @SerializedName("pendingCollections")
    var pendingCollections: String? = null
}