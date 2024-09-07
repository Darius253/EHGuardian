package com.example.ehguardian.data.repositories


import com.example.ehguardian.data.models.MeasurementData
import com.example.ehguardian.data.models.UserModel
import com.example.ehguardian.data.services.Authentication
import kotlinx.coroutines.flow.Flow

class FirebaseUserRepository(
    private val authService: Authentication,

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
                onComplete(true, userId)
            } else {
                onComplete(false, userId)
            }
        }
    }

    override suspend fun signOut(){
        authService.signOut()

    }

    // Implement other methods of UserRepository
    override fun getUser(): Flow<UserModel> {
        TODO("Not yet implemented")
    }




    override suspend fun updateUserDetails(user: UserModel) = TODO()

    override suspend fun deleteAccount(user: UserModel) = TODO()
    override suspend fun uploadUserMeasurement(measurement: MeasurementData) = TODO()
    override fun getUserMeasurements(): Flow<List<MeasurementData>> = TODO()
    override suspend fun getUserLatestMeasurementByUserId(userId: Int): List<MeasurementData> = TODO()
}
