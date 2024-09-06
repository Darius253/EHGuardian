package com.example.ehguardian.data.repositories

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.ehguardian.data.models.MeasurementData
import com.example.ehguardian.data.models.UserModel
import com.example.ehguardian.data.services.Authentication
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class FirebaseUserRepository(
    private val authService: Authentication,
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun userSignIn(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        authService.signIn(email, password) { success, userId ->
            if (success) {

        onComplete(true, userId)


            } else {
             onComplete(false, userId)

            }
        }
    }


    override fun userSignUp(user: UserModel, onComplete: (Boolean, String?) -> Unit) {
        authService.signUp(
            user,

        ) { success, userId ->
            if (success && userId != null) {
                user.id = userId.toInt()
                firestore.collection("users").document(userId).set(user)
            } else {
                onComplete(false, userId)
            }
        }
    }

    // Implement other methods of UserRepository
    override fun getUser(): Flow<UserModel> {
        TODO("Not yet implemented")
    }




    override suspend fun updateUserDetails(user: UserModel) = TODO()
    override suspend fun signOut() = TODO()
    override suspend fun deleteAccount(user: UserModel) = TODO()
    override suspend fun uploadUserMeasurement(measurement: MeasurementData) = TODO()
    override fun getUserMeasurements(): Flow<List<MeasurementData>> = TODO()
    override suspend fun getUserLatestMeasurementByUserId(userId: Int): List<MeasurementData> = TODO()
}
