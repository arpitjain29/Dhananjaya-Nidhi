package com.dhananjayanidhi.models.transaction

import com.google.gson.annotations.SerializedName

@Suppress("unused")
class DataTransactionModel {
    @SerializedName("current_page")
    var currentPage: Long? = null

    @SerializedName("data")
    var data: MutableList<DatumTransactionModel>? = null

    @SerializedName("first_page_url")
    var firstPageUrl: String? = null

    @SerializedName("from")
    var from: Long? = null

    @SerializedName("last_page")
    var lastPage: Long? = null

    @SerializedName("last_page_url")
    var lastPageUrl: String? = null

    @SerializedName("links")
    var links: List<LinkTransactionModel>? = null

    @SerializedName("next_page_url")
    var nextPageUrl: Any? = null

    @SerializedName("path")
    var path: String? = null

    @SerializedName("per_page")
    var perPage: Long? = null

    @SerializedName("prev_page_url")
    var prevPageUrl: Any? = null

    @SerializedName("to")
    var to: Long? = null

    @SerializedName("total")
    var total: Long? = null
}
