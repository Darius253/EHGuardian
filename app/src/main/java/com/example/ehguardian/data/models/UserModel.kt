package com.example.ehguardian.data.models
import android.net.Uri
import java.io.Serializable


data class UserModel(
    var email: String = "",
    var gender: String,
    var password: String = "",
    var firstname: String,
    var lastname: String,
    var userWeight: String,
    var userHeight: String,
    var dateOfBirth: Serializable,
    var createdDate: Serializable,
    var cholesterolLevel:String,
    var bloodSugarLevel: String,
    var userImage: String,


    )
