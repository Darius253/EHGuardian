package com.trontech.ehguardian

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.trontech.ehguardian.data.repositories.FirebaseUserRepository
import com.trontech.ehguardian.data.repositories.UserRepository
import com.trontech.ehguardian.data.services.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.trontech.ehguardian.data.repositories.UserPreferencesRepository


private const val PUSHED_NOTIFICATION_PREFERENCE_NAME = "pushed_notification_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PUSHED_NOTIFICATION_PREFERENCE_NAME
)

class EhGuardianApplication : Application() {

    lateinit var container: AppContainer
     lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
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
