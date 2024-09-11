package com.example.ehguardian.network

import com.example.ehguardian.data.models.HealthNewsModel
import com.example.ehguardian.data.models.NewsRequest
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


// Base URL of your API


// Define your Retrofit API interface
// Retrofit API Service
interface NewsApiService {
    @Headers(
        "x-rapidapi-key: 03ef9b87e2mshcf6283f5332f6eep1504b5jsnd62b13d8370c",
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

