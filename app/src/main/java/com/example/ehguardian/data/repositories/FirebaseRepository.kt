package com.example.ehguardian.data.repositories



import android.util.Log
import com.example.ehguardian.data.models.MeasurementData
import com.example.ehguardian.data.models.NewsItem
import com.example.ehguardian.data.models.UserModel
import com.example.ehguardian.data.services.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirebaseUserRepository(
    private val userService: User,

    ) : UserRepository {

    override fun userSignIn(
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        userService.signIn(email, password) { success, userId ->
            if (success) {
                onComplete(true, userId)

            } else {
                onComplete(false, userId)

            }
        }
    }


    override fun userSignUp(user: UserModel, onComplete: (Boolean, String?) -> Unit) {
        userService.signUp(
            user,
        ) { success, userId ->
            if (success && userId != null) {
                onComplete(true, userId)
            } else {
                onComplete(false, userId)
            }
        }
    }

    override suspend fun signOut(
       onSignOutSuccess: (Boolean) -> Unit
    ) {
        try {

            userService.signOut()
            onSignOutSuccess(true)

        }
        catch (e: Exception) {

            onSignOutSuccess(false)
        }

    }

    // Implement other methods of UserRepository

    override suspend fun getUser(): Flow<UserModel> {
        return flow {
            try {
                // Fetch user details from the service
                val userDetails = userService.getUser() ?: throw Exception("User not found")

                // Emit the user details as a UserModel
                emit(userDetails)
            } catch (e: Exception) {
                // Handle any errors that occur during fetching
                throw Exception("Failed to fetch user: ${e.message}")
            }
        }
    }


    override suspend fun updateUserDetails(user: UserModel): Boolean {
        return try {
            // Call the updateUserDetails function from your service
            val success = userService.updateUserDetails(user)
            success

        } catch (e: Exception) {

            // Handle the exception and log it
            throw e
            // Optionally rethrow the exception if necessary
        }
    }


    override suspend fun uploadUserMeasurement(measurementData: MeasurementData): Boolean {
        return try {
            // Call the actual upload service
            val success = userService.uploadUserMeasurement(measurementData)

            // Return success result
            success
        } catch (e: Exception) {
            // Log and handle failure
            e.printStackTrace()
            false
        }
    }


    override suspend fun deleteAccount(user: UserModel) = TODO()




    override suspend fun getUserMeasurements(): Flow<List<MeasurementData>> {
        return flow {
            try {
                 userService.getUserMeasurements().collect{
                    emit(it)

                }


            } catch (e: Exception) {
                Log.e("MeasurementFlow", "Failed to fetch user measurements: ${e.message}", e)
                emit(emptyList())
            }
        }
    }



    override suspend fun fetchHealthNews(): List<NewsItem> {
        return try {
            val news = userService.fetchHealthNews()
            news
        } catch (e: Exception) {
            Log.e("NewsFlow", "Failed to fetch health news: ${e.message}", e)
            emptyList()
        }
    }



}
