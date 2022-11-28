package com.pandadevs.heyfix_client.data.model

data class NotificationModel(
    var to: String,
    var data: HashMap<String, String>
)