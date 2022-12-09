package com.pandadevs.heyfix_client.data.model

import com.google.firebase.Timestamp

data class HistorialServiceModel(
    var worker_name: String,
    var category_name: String,
    var address: String,
    var canceled: Boolean,
    var completed: Boolean,
    var date_hired: Timestamp,
)