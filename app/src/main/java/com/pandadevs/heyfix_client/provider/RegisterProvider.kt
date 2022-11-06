package com.pandadevs.heyfix_client.provider

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.pandadevs.heyfix_client.data.model.UserPost
import kotlinx.coroutines.CompletableDeferred


class RegisterProvider {

    companion object{
        suspend fun registerDataUser(user:UserPost, password:String):String?{
            val def = CompletableDeferred<String?>()
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document()
                .set(user)
                .addOnSuccessListener {
                    // Save data in Firebase Auth
                    FirebaseAuth
                        .getInstance()
                        .createUserWithEmailAndPassword(user.email,password)
                        .addOnCompleteListener{
                            def.complete(if (it.isSuccessful) "success" else "error")
                        }
                }
                .addOnFailureListener {
                    def.complete("error")
                }
            return def.await()
        }
    }

}

