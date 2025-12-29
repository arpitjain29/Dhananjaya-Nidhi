package com.dhananjayanidhi.utils

import android.content.Context
import com.dhananjayanidhi.R
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response

/**
 * Helper class for handling API errors consistently
 * Provides reusable error handling logic to reduce code duplication
 */
object ErrorHandler {

    /**
     * Handles API error response and shows appropriate error message
     * @param context The context for showing toast
     * @param response The error response
     * @param defaultMessage Default message if error parsing fails
     * @param onError Optional callback for additional error handling
     */
    fun handleErrorResponse(
        context: Context?,
        response: Response<*>?,
        defaultMessage: String? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (context == null) return

        val errorMessage = parseErrorMessage(response) 
            ?: defaultMessage 
            ?: context.getString(R.string.error_occurred)
        CommonFunction.showToastSingle(context, errorMessage, 0)
        onError?.invoke(errorMessage)
    }

    /**
     * Parses error message from API response
     * @param response The error response
     * @return Parsed error message or null if parsing fails
     */
    fun parseErrorMessage(response: Response<*>?): String? {
        return try {
            val errorBody = response?.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                val errorJson = JSONObject(errorBody)
                val errorArray = errorJson.optJSONArray("error")
                if (errorArray != null && errorArray.length() > 0) {
                    errorArray.getJSONObject(0).optString("message", null)
                } else {
                    errorJson.optString("message", null)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Handles API failure (network errors, etc.)
     * @param context The context for showing toast
     * @param throwable The throwable error
     * @param onError Optional callback for additional error handling
     */
    fun handleFailure(
        context: Context?,
        throwable: Throwable,
        onError: ((Throwable) -> Unit)? = null
    ) {
        throwable.printStackTrace()
        onError?.invoke(throwable)

        if (throwable is HttpException) {
            // HTTP errors are typically handled by handleErrorResponse
            // This is for other network failures
            if (context != null) {
                CommonFunction.showToastSingle(
                    context,
                    context.getString(R.string.net_connection),
                    0
                )
            }
        }
    }

    /**
     * Checks if response is successful and status code is 200
     * @param response The response to check
     * @return True if successful
     */
    fun isSuccessResponse(response: Response<*>?): Boolean {
        return response?.isSuccessful == true
    }
}
