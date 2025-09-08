package com.dhananjayanidhi.models.customerlistv1

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataCustomerListV1Model {
    @SerializedName("current_page")
    @Expose
    var currentPage: String? = null

    @SerializedName("data")
    @Expose
    var data: List<DatumCustomerListV1Model>? = null

    @SerializedName("first_page_url")
    @Expose
    var firstPageUrl: String? = null

    @SerializedName("from")
    @Expose
    var from: Int? = null

    @SerializedName("last_page")
    @Expose
    var lastPage: String? = null

    @SerializedName("last_page_url")
    @Expose
    var lastPageUrl: String? = null

    @SerializedName("links")
    @Expose
    var links: List<LinkCustomerListV1Model>? = null

    @SerializedName("next_page_url")
    @Expose
    var nextPageUrl: String? = null

    @SerializedName("path")
    @Expose
    var path: String? = null

    @SerializedName("per_page")
    @Expose
    var perPage: String? = null

    @SerializedName("prev_page_url")
    @Expose
    var prevPageUrl: Any? = null

    @SerializedName("to")
    @Expose
    var to: Int? = null

    @SerializedName("total")
    @Expose
    var total: String? = null
}
