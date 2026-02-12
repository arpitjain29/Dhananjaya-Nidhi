package com.dhananjayanidhi.models.agentpayout

import com.dhananjayanidhi.models.CommonModel
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class AgentMonthlyPayoutModel : CommonModel() {
    @SerializedName("data")
    var data: List<AgentMonthlyPayoutItem>? = null
}

@Suppress("unused")
data class AgentMonthlyPayoutItem(
    @SerializedName("month_name")
    val monthName: String?,
    @SerializedName("total_month_collection")
    val totalMonthCollection: Int?,
    @SerializedName("total_payout")
    val totalPayout: Int?
)

