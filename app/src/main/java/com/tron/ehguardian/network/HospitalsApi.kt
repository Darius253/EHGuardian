package com.trontech.ehguardian.network

import com.trontech.ehguardian.data.models.HospitalSearchTextRequest
import com.trontech.ehguardian.data.models.HospitalsModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HospitalsApi {

    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "X-Goog-Api-Key: AIzaSyBQ2I9uh_6cLB2_owrWl_BMWy0MmHWt8HI",  // Replace with actual API Key
        "X-Goog-FieldMask: places.displayName,places.shortFormattedAddress," +
                "places.rating,places.nationalPhoneNumber,places.googleMapsUri,places.businessStatus"
    )
    @POST("v1/places:searchText?fields=*")
    suspend fun getHospitals(@Body request: HospitalSearchTextRequest): retrofit2.Response<HospitalsModel>
}

object HospitalsApiInstance {
    private const val BASE_URL = "https://places.googleapis.com/"

    val api: HospitalsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HospitalsApi::class.java)
    }
}
