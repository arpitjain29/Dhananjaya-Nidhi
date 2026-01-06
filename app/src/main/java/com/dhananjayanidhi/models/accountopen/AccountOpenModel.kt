package com.dhananjayanidhi.models.accountopen

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

@Suppress("unused")
class AccountOpenModel {
    @SerializedName("data")
    @JsonAdapter(DataAccountOpenAdapter::class)
    var data: List<DataAccountOpenModel>? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("success")
    var success: Boolean? = null
}

/**
 * Custom deserializer to handle both object and array for data field
 */
class DataAccountOpenAdapter : JsonDeserializer<List<DataAccountOpenModel>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<DataAccountOpenModel>? {
        if (json == null || json.isJsonNull) {
            return null
        }
        
        return try {
            when {
                json.isJsonArray -> {
                    // Handle array case
                    val list = mutableListOf<DataAccountOpenModel>()
                    json.asJsonArray.forEach { element ->
                        context?.deserialize<DataAccountOpenModel>(element, DataAccountOpenModel::class.java)?.let {
                            list.add(it)
                        }
                    }
                    list
                }
                json.isJsonObject -> {
                    // Handle object case - wrap in list
                    val obj = context?.deserialize<DataAccountOpenModel>(json, DataAccountOpenModel::class.java)
                    if (obj != null) listOf(obj) else emptyList()
                }
                else -> emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
