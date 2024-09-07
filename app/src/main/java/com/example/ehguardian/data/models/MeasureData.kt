package com.example.ehguardian.data.models

data class MeasurementData(
    var id: Int = 0,
    var systolic: String = "",
    var diastolic: String ="",
    var heartRate: String="",
    var timestamp: String="",
    var userId: Int=0,
    var cholesterolLevel: String="",
    var bloodSugarLevel: String="",
    var dateStamp: String="",


)
