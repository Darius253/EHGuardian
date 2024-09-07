package com.example.ehguardian.data.services


import com.example.ehguardian.data.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Authentication(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore) {

    fun signUp(
        userModel: UserModel,
        onComplete: (Boolean, String?) -> Unit,


    ) {
        auth.createUserWithEmailAndPassword(userModel.email, userModel.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    firestore.collection("users").document(userId!!).set(userModel)
                    onComplete(true, auth.currentUser?.uid)

                } else {
                    onComplete(false, task.exception?.message)

                }
            }
    }

    fun signIn(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, auth.currentUser?.uid)
                } else {
                    onComplete(false, task.exception?.message)


                }
            }
    }

    fun signOut(
    ) {
        auth.signOut()
    }
}
