package com.pandadevs.heyfix_client.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class UserGet(
    var id: String,
    var name: String,
    var first_surname: String,
    var second_surname: String,
    var active: Boolean,
    var client: Boolean,
    var email: String,
    var phone_number: String,
    var picture: String,
    var ranked_avg: Double,
    var transport: String,
    var category_id: String,
    var tokenNotification: String
)