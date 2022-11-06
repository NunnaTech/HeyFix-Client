package com.pandadevs.heyfix_client.provider

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CompletableDeferred

class LoginProvider {
    companion object{
        suspend fun LoginWithEmail(email:String,password:String):Boolean?{
            val response = CompletableDeferred<Boolean?>()
            FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        response.complete(true)
                    }else{
                        response.complete(false)
                    }
                }
            return response.await()
        }
    }
}