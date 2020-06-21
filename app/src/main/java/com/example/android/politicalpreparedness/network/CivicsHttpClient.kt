package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.network.CivicsHttpClient.Companion.API_KEY
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class CivicsHttpClient: OkHttpClient() {

    companion object {

        var API_KEY = BuildConfig.API_KEY

        fun getClient(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            return Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val url = original
                                .url
                                .newBuilder()
                                .addQueryParameter("key", API_KEY)
                                .build()
                        val request = original
                                .newBuilder()
                                .url(url)
                                .build()
                        chain.proceed(request)
                    }
                    .addInterceptor(interceptor)
                    .build()
        }

    }

}