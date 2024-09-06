package com.example.ehguardian.data.repositories

import android.content.Context
import com.example.ehguardian.data.models.MeasurementData
import com.example.ehguardian.data.models.UserModel
import kotlinx.coroutines.flow.Flow

interface  UserRepository{

    fun userSignIn(email:String, password:String)

    fun userSignUp(user:UserModel)

    fun getUser(): Flow<UserModel>

    suspend fun updateUserDetails(user: UserModel)

    suspend fun signOut()

    suspend fun deleteAccount(user: UserModel)

    suspend fun uploadUserMeasurement(measurement: MeasurementData)

    fun getUserMeasurements(): Flow<List<MeasurementData>>

    suspend fun getUserLatestMeasurementByUserId(userId: Int): List<MeasurementData>

}
