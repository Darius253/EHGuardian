package com.example.ehguardian.network

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


// Base URL of your API
private const val BASE_URL = "https://newsnow.p.rapidapi.com/"

// Define your Retrofit API interface
interface NewsApiService {

    @Headers(
        "x-rapidapi-key: 03ef9b87e2mshcf6283f5332f6eep1504b5jsnd62b13d8370c", // Replace with your actual API key
        "x-rapidapi-host: newsnow.p.rapidapi.com",
        "Content-Type: application/json"
    )
    @POST("newsv2_top_news_cat")
    fun getNews(@Body body: String): Call<String>  // Expecting a raw string response
}

// Retrofit instance
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())  // Scalars converter for plain text
    .build()

// Singleton for accessing the API
object NewsApi {
    val retrofitService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}
