package com.trontech.ehguardian.data.repositories



import android.content.Context
import com.trontech.ehguardian.data.models.HospitalItem
import com.trontech.ehguardian.data.models.MeasurementData
import com.trontech.ehguardian.data.models.NewsItem
import com.trontech.ehguardian.data.models.UserModel
import kotlinx.coroutines.flow.Flow

interface  UserRepository{

    fun userSignIn(email:String, password:String, onComplete: (Boolean, String?) -> Unit)

    fun userSignUp(user:UserModel, onComplete: (Boolean, String?) -> Unit)

    suspend fun getUser(): Flow<UserModel>

    suspend fun updateUserDetails(user: UserModel):Boolean

    suspend fun signOut():Boolean

    suspend fun deleteAccount():Boolean

    suspend fun uploadUserMeasurement(measurementData: MeasurementData):Boolean


    suspend fun getUserMeasurements(): Flow<List<MeasurementData>>

    suspend fun fetchNearbyHospitals(context: Context):List<HospitalItem>

    suspend fun fetchHealthNews():List<NewsItem>

}
