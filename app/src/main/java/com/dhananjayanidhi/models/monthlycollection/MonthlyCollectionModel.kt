package com.dhananjayanidhi.models.monthlycollection

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class MonthlyCollectionModel : CommonModel() {
    @SerializedName("data")
    var data: MonthlyCollectionData? = null
}

@Suppress("unused")
data class MonthlyCollectionData(
    @SerializedName("total_collection")
    val totalCollection: Int?,
    @SerializedName("datewise_collection")
    val datewiseCollection: List<DatewiseCollectionItem>?,
    @SerializedName("customerwise_collection")
    val customerwiseCollection: List<CustomerwiseCollectionItem>?
)

@Suppress("unused")
data class DatewiseCollectionItem(
    @SerializedName("date")
    val date: String?,
    @SerializedName("amount")
    val amount: Int?
)

@Suppress("unused")
data class CustomerwiseCollectionItem(
    @SerializedName("customer_id")
    val customerId: Int?,
    @SerializedName("customer_name")
    val customerName: String?,
    @SerializedName("amount")
    val amount: Int?
)

