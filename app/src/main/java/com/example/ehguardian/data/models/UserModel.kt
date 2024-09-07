package com.example.ehguardian.data.models
import java.io.Serializable


data class UserModel(
    var id: String,
    var email: String,
    var gender: String,
    var password: String,
    var firstname: String,
    var lastname: String,
    var userWeight: String,
    var userHeight: String,
    var dateOfBirth: Serializable,
    var createdDate: Int,
    var measurementData: MeasurementData,
    )
