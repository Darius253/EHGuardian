package com.tron.ehguardian.network

import com.tron.ehguardian.data.models.HealthNewsModel
import com.tron.ehguardian.data.models.NewsRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit




// Define your Retrofit API interface
// Retrofit API Service
interface NewsApiService {
    @Headers(
        "x-rapidapi-key: b532490f19msh7ff0655157640cdp1087eejsnc94c91f1b7b4",
        "x-rapidapi-host: newsnow.p.rapidapi.com",
        "Content-Type: application/json"
    )
    @POST("newsv2_top_news_cat")
    suspend fun getNews(@Body body: NewsRequest): Response<HealthNewsModel>
}




private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(2, TimeUnit.MINUTES)  // Connection timeout
    .writeTimeout(2, TimeUnit.MINUTES)     // Write timeout
    .readTimeout(2, TimeUnit.MINUTES)      // Read timeout
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("https://newsnow.p.rapidapi.com/")
    .client(okHttpClient)  // Set custom OkHttpClient
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object NewsApi {
    val retrofitService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}

