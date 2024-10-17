package com.trontech.ehguardian.data.models

import com.google.gson.annotations.SerializedName

data class HospitalSearchTextRequest(
    val textQuery: String,
    val openNow: Boolean,
    val pageSize: Int,
    val locationBias: LocationBias
)

data class LocationBias(
    val circle: Circle
)

data class Circle(
    val center: CircleCenter,
//    val radius: Double
)

data class CircleCenter(
    val latitude: Double,
    val longitude: Double
)


data class HospitalsModel(
    @SerializedName("places")
    val hospitals: List<HospitalItem>? = null,


)

data class DisplayName(
    @SerializedName("text")
    val name: String
)


data class HospitalItem(
    @SerializedName("displayName")
    val displayName: DisplayName,

    @SerializedName("shortFormattedAddress")
    val address: String,

    @SerializedName("rating")
    val rating: String? = null,



    @SerializedName("nationalPhoneNumber")
    val phone: String,

    @SerializedName("googleMapsUri")
    val googleMapsUri: String,

    @SerializedName("businessStatus")
    val businessStatus: String
)




