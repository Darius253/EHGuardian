package com.trontech.ehguardian

import android.app.Application
import com.trontech.ehguardian.data.repositories.FirebaseUserRepository
import com.trontech.ehguardian.data.repositories.UserRepository
import com.trontech.ehguardian.data.services.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EhGuardianApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        container = AppDataContainer()
    }
}

interface AppContainer {
    val userRepository: UserRepository

}

class AppDataContainer : AppContainer {
    // Provide Firebase services
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Providing the concrete implementation of UserRepository
    override val userRepository: UserRepository by lazy {
        FirebaseUserRepository(
            userService = User(
                auth = firebaseAuth,
                firestore = firestore
            ),

        )
    }
}
