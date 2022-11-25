package com.pandadevs.heyfix_client.provider

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.pandadevs.heyfix_client.utils.SharedPreferenceManager

class UserLastProvider {

    companion object {
        fun setLastOnline(context: Context): Unit {
            val userId = SharedPreferenceManager(context).getUser()!!.id
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(userId)
                .update(mapOf("last_online" to Timestamp.now()))
        }

        fun setLastCurrentPosition(context: Context, location: GeoPoint) {
            val userId = SharedPreferenceManager(context).getUser()!!.id
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(userId)
                .update(mapOf("current_position" to location))
        }

    }
}