package com.pandadevs.heyfix_client.provider

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CompletableDeferred

class LoginProvider {
    companion object{
        suspend fun loginWithEmail(email:String,password:String):Boolean?{
            val response = CompletableDeferred<Boolean?>()
            FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    response.complete(it.isSuccessful)
                }
            return response.await()
        }
    }
}