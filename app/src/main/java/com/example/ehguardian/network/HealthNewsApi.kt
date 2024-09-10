package com.example.ehguardian.network

import com.example.ehguardian.data.models.HealthNewsModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


// Base URL of your API
private const val BASE_URL = "https://newsnow.p.rapidapi.com/"

// Define your Retrofit API interface
// Retrofit API Service
interface NewsApiService {

    @Headers(
        "x-rapidapi-key: YOUR_API_KEY",
        "x-rapidapi-host: newsnow.p.rapidapi.com",
        "Content-Type: application/json"
    )
    @POST("newsv2_top_news_cat")
    fun getNews(@Body body: String): HealthNewsModel
}

// Retrofit instance with Gson converter
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())  // Gson converter for JSON parsing
    .build()


// Singleton for accessing the API
object NewsApi {
    val retrofitService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}
