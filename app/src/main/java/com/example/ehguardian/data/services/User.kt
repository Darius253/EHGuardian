package com.example.ehguardian.data.services


import android.util.Log
import com.example.ehguardian.data.models.MeasurementData
import com.example.ehguardian.data.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class User(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    fun signUp(
        userModel: UserModel,
        onComplete: (Boolean, String?) -> Unit,


        ) {
        auth.createUserWithEmailAndPassword(userModel.email, userModel.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    firestore.collection("users").document(userId!!).set(userModel)
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
                        id = data?.get("id") as? String ?: ""
                    )
                }
            } catch (e: Exception) {
                // Handle the error, log it or return null
                e.printStackTrace()
            }
        }
        return null
    }


    suspend fun updateUserDetails(userModel: UserModel): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            return try {
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
                            "cholesterolLevel" to userModel.cholesterolLevel
                        )
                    ).await()  // Await to ensure it's executed in the coroutine

                true  // Return true if successful
            } catch (e: Exception) {
                e.printStackTrace()
                false  // Return false if an error occurs
            }
        }
        return false  // Return false if no current user is found
    }


    suspend fun uploadUserMeasurement(measurementData: MeasurementData): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return if (user != null) {
            try {
                // Get reference to the user's document
                val userDocRef = firestore.collection("users").document(user.uid)

                // Add the measurement data to a new "measurements" sub-collection
                val measurementRef = userDocRef.collection("measurements").document()

                // Upload the data
                measurementRef.set(measurementData).await()

                true // Return true if upload was successful
            } catch (e: Exception) {
                e.printStackTrace()
                false // Return false if there was an error
            }
        } else {
            false // Return false if the user is not authenticated
        }
    }


    suspend fun getUserMeasurements(): Flow<List<MeasurementData>> {
        return flow {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                try {
                    val userDocRef = firestore.collection("users").document(user.uid)
                    val measurementsQuery = userDocRef.collection("measurements")
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

     suspend fun getUserLatestMeasurement(): Flow<MeasurementData?> {
        return flow {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                try {
                    // Access the user's document in Firestore
                    val userDocRef = firestore.collection("users").document(user.uid)

                    // Query to get the latest measurement ordered by timestamp
                    val measurementsQuery = userDocRef.collection("measurements")
                        .orderBy("timestamp", Query.Direction.ASCENDING) // Ensure the latest comes first
                        .limit(1)

                    // Await the result of the query
                    val querySnapshot = measurementsQuery.get().await()

                    // Check if any document exists
                    if (querySnapshot.documents.isNotEmpty()) {
                        // Convert the document to MeasurementData
                        val latestMeasurement = querySnapshot.documents[0].toObject(MeasurementData::class.java)
                        emit(latestMeasurement)
                    } else {
                        emit(null) // No measurement found
                    }
                } catch (e: Exception) {
                    Log.e("FirestoreError", "Error fetching latest measurement: ${e.message}")
                    emit(null) // Emit null in case of an error
                }
            } else {
                Log.e("AuthError", "User is not authenticated")
                emit(null) // Emit null if the user is not authenticated
            }
        }
    }

}






