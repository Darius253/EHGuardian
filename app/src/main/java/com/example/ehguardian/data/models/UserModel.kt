package com.example.ehguardian.data.models
import android.net.Uri
import java.io.Serializable


data class UserModel(
    var id: String,
    var email: String = "",
    var gender: String,
    var password: String = "",
    var firstname: String,
    var lastname: String,
    var userWeight: String,
    var userHeight: String,
    var dateOfBirth: Serializable,
    var createdDate: Serializable = System.currentTimeMillis(),
    var cholesterolLevel:String,
    var bloodSugarLevel: String,
    var userImage: Uri? = null,


    )
