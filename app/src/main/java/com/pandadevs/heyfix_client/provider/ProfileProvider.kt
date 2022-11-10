package com.pandadevs.heyfix_client.provider

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pandadevs.heyfix_client.data.model.UserGet
import com.pandadevs.heyfix_client.utils.SharedPreferenceManager
import com.pandadevs.heyfix_client.utils.datatype.Result
class ProfileProvider {
    companion object{
        fun uploadUserImage(imageUri: Uri): Result<String>{
            var result: Result<String>? =Result.success("data updated")
            val imageFileName = "profile-picture/${System.currentTimeMillis()}.png"
            System.out.println("Loya: name:"+imageFileName)

            FirebaseStorage.getInstance().reference.child(imageFileName).putFile(imageUri)
                .addOnSuccessListener {
                    System.out.println("Loya: it:"+it)
                    System.out.println("Loya: it:"+it.uploadSessionUri)
                    System.out.println("Loya: it:"+it.storage)
                    System.out.println("Loya: it:"+it.metadata)

                    result = Result.success(it.uploadSessionUri.toString())
                }
                .addOnFailureListener {
                    result = Result.error(it.message)
                }
            return result!!
        }
        fun updateUserData(user: UserGet): Result<String>{
            var result: Result<String>? = Result.success("data updated")
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(user.id)
                .set(user).addOnCompleteListener {
                    result = Result.success("data updated")
                }.addOnFailureListener {
                    result = Result.error("error: $it")
                }
            return result!!
        }
    }
}