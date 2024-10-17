package com.trontech.ehguardian.data.services


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.trontech.ehguardian.data.models.Circle
import com.trontech.ehguardian.data.models.CircleCenter
import com.trontech.ehguardian.data.models.HospitalItem
import com.trontech.ehguardian.data.models.HospitalSearchTextRequest
import com.trontech.ehguardian.data.models.LocationBias
import com.trontech.ehguardian.data.models.MeasurementData
import com.trontech.ehguardian.data.models.NewsItem
import com.trontech.ehguardian.data.models.NewsRequest
import com.trontech.ehguardian.data.models.UserModel
import com.trontech.ehguardian.network.HospitalsApiInstance
import com.trontech.ehguardian.network.NewsApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import kotlin.coroutines.resume

class User(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    fun signUp(
        userModel: UserModel,
        onComplete: (Boolean, String?) -> Unit,


        ) {
        auth.createUserWithEmailAndPassword(userModel.email, userModel.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    firestore.collection("users").document(userId!!).set(
                        mapOf(
                            "firstname" to userModel.firstname,
                            "lastname" to userModel.lastname,
                            "gender" to userModel.gender,
                            "userWeight" to userModel.userWeight,
                            "userHeight" to userModel.userHeight,
                            "dateOfBirth" to userModel.dateOfBirth,
                            "bloodSugarLevel" to userModel.bloodSugarLevel,
                            "cholesterolLevel" to userModel.cholesterolLevel,
                            "id" to userId,
                            "userImage" to userModel.userImage,
                            "email" to userModel.email,
                            "password" to userModel.password,
                            "createdAt" to Calendar.getInstance().time,

                        )

                    )
                    onComplete(true, auth.currentUser?.uid)

                } else {
                    onComplete(false, task.exception?.message)

                }
            }
    }

    fun signIn(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, auth.currentUser?.uid)
                } else {
                    onComplete(false, task.exception?.message)


                }
            }
    }

    fun signOut(
    ) {
        auth.signOut()
    }


    suspend fun getUser(): UserModel? {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Reference to the Firestore document

            val userDoc = firestore.collection("users").document(user.uid)
            try {
                // Fetch the document asynchronously using coroutines
                val snapshot = userDoc.get().await()

                // Map Firestore document to UserModel if the document exists
                if (snapshot.exists()) {
                    val data = snapshot.data
                    return UserModel(
                        firstname = data?.get("firstname") as? String ?: "",
                        lastname = data?.get("lastname") as? String ?: "",
                        gender = data?.get("gender") as? String ?: "",
                        userWeight = data?.get("userWeight") as? String ?: "",
                        userHeight = data?.get("userHeight") as? String ?: "",
                        dateOfBirth = data?.get("dateOfBirth") as? String ?: "",
                        bloodSugarLevel = data?.get("bloodSugarLevel") as? String
                            ?: "",  // Fixed here
                        cholesterolLevel = data?.get("cholesterolLevel") as? String ?: "",
                        userImage = data?.get("userImage") as? String ?: "",
                        createdDate = data?.get("createdDate") as? String ?: "",
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }


    suspend fun updateUserDetails(userModel: UserModel): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
       val  storageRef = Firebase.storage.reference
        if (user != null) {
             try {
                 val file = Uri.parse(userModel.userImage)

                 val userProfileImageRef = storageRef
                     .child("profile_images/${user.uid}${file.lastPathSegment}")
                 val uploadTask = userProfileImageRef.putFile(file)



                 val urlTask = uploadTask.continueWithTask { task ->
                     if (!task.isSuccessful) {
                         task.exception?.let {
                             throw it
                         }
                     }
                     userProfileImageRef.downloadUrl
                 }.addOnCompleteListener { task ->
                     if (task.isSuccessful) {
                         val downloadUri = task.result
                         userModel.userImage = downloadUri.toString()
                     }
                 }
                 urlTask.await()



                 // Update the user document with new details in Firestore
                firestore.collection("users").document(user.uid)
                    .update(
                        mapOf(
                            "firstname" to userModel.firstname,
                            "lastname" to userModel.lastname,
                            "gender" to userModel.gender,
                            "userWeight" to userModel.userWeight,
                            "userHeight" to userModel.userHeight,
                            "dateOfBirth" to userModel.dateOfBirth,
                            "bloodSugarLevel" to userModel.bloodSugarLevel,
                            "cholesterolLevel" to userModel.cholesterolLevel,
                            "userImage" to urlTask.result),

                    ).await()  // Await to ensure it's executed in the coroutine

               return  true  // Return true if successful
            } catch (e: Exception) {

                e.printStackTrace()

                return false  // Return false if an error occurs
            }
        }
        return false  // Return false if no current user is found
    }


    suspend fun uploadUserMeasurement(measurementData: MeasurementData): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
         if (user != null) {
            try {
                // Get reference to the user's document
                val userDocRef = firestore.collection("users").document(user.uid)

                // Add the measurement data to a new "measurements" sub-collection
                val measurementRef = userDocRef.collection("measurements").document()

                // Upload the data
                measurementRef.set(measurementData).await()

                return true // Return true if upload was successful
            } catch (e: Exception) {
                e.printStackTrace()
               return false // Return false if there was an error
            }
        } else {
            return false // Return false if the user is not authenticated
        }
    }


     fun getUserMeasurements(): Flow<List<MeasurementData>> {
        return flow {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                try {
                    val userDocRef = firestore.collection("users").document(user.uid)
                    val measurementsQuery = userDocRef.collection("measurements")
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .await() // Make sure you use the 'await' function if you're using Kotlin Coroutines

                    val measurements = measurementsQuery.documents.mapNotNull { document ->
                        document.toObject(MeasurementData::class.java)
                    }

                    emit(measurements)

                } catch (e: Exception) {
                    emit(emptyList())


                }
            } else {
                emit(emptyList())
            }
        }
    }



    suspend fun fetchHealthNews(): List<NewsItem> {
        val requestBody = NewsRequest(
            category = "HEALTH",
            location = "",
            language = "en",
            page = 1
        )

        return try {
            val response = NewsApi.retrofitService.getNews(requestBody)
            if (response.isSuccessful) {
                Log.d("NewsFlow", "Response: ${response.body()}")
                val body = response.body()
                body?.news ?: emptyList()
            } else {
                Log.e("NewsFlow", "Response Error: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("NewsFlow", "Failed to fetch health news: ${e.message}", e)
            emptyList()
        }
    }




    @SuppressLint("MissingPermission")
    private suspend fun getLocation(context: Context): Location = suspendCancellableCoroutine { continuation ->
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        var location: Location
        val locationListener = object : LocationListener {
            override fun onLocationChanged(newLocation: Location) {
                location = newLocation
                locationManager.removeUpdates(this)
                continuation.resume(location)
            }
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (hasGps || hasNetwork) {
            if (hasGps) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    locationListener
                )
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    locationListener
                )
            }
            continuation.invokeOnCancellation {
                locationManager.removeUpdates(locationListener)
            }
        } else {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    suspend fun fetchNearbyHospitals(context: Context): List<HospitalItem> {
        val location = getLocation(context)
        val requestBody = HospitalSearchTextRequest(
            textQuery = "Healthcare centers and Hospitals",
            openNow = true,
            pageSize = 15,
            locationBias = LocationBias(
                circle = Circle(
                    center = CircleCenter(
                        latitude = location.latitude,
                        longitude = location.longitude
                    ),
                )
            )
        )
        return try {
            val response = HospitalsApiInstance.api.getHospitals(requestBody)
            if (response.isSuccessful) {
                val body = response.body()
                body?.hospitals ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("HospitalFlow", "Failed to fetch nearby hospitals: ${e.message}", e)
            emptyList()
        }
    }


}






