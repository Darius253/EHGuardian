package com.example.ehguardian.data.models


data class UserModel(
    var id: Int,
    var name: String,
    var email: String,
    var gender: String,
    var status: String,
    var createdAt: String,
    var password: String,
    var firstName: String,
    var lastName: String,
    var userWeight: String,
    var userHeight: String,
    var dateOfBirth: String,
    var measurementData: MeasurementData,
    )
