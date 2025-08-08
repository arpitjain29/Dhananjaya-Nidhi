package com.dhananjayanidhi.apiUtils

import android.app.Activity
import com.dhananjayanidhi.utils.AppController
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {

    fun buildService(mContext: Activity?): ApiInterface {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val interceptorToken = TokenInterceptor()
        val client = OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(interceptor).addInterceptor(interceptorToken).build()


        return Retrofit.Builder().baseUrl(ApiUrlEndpoint.BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiInterface::class.java)
    }

    class TokenInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            //rewrite the request to add bearer token
            val newRequest: Request =
                if ( AppController.instance?.sessionManager?.getLoginModel != null) {
                    chain.request().newBuilder().header("Content-Type", "application/json")
                        .header("Accept", "application/json").header(
                            "authorization", String.format(
                                "%s %s",
                                AppController.instance?.sessionManager?.getLoginModel?.accessToken?.type ?: "",
                                AppController.instance?.sessionManager?.getLoginModel?.accessToken?.token ?: ""
                            )
                        ).build()
                } else {
                    chain.request().newBuilder().header("Accept", "application/json")
                        .header("Content-Type", "application/json").build()
                }
            return chain.proceed(newRequest)
        }
    }
}