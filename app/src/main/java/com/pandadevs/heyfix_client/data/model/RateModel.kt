package com.pandadevs.heyfix_client.data.model

import java.io.Serializable

data class RateModel(
    var id: String,
    var worker_name: String,
    var category_name: String,
    var category_icon: String,
    var worker_transport: String,
    var worker_picture: String
) : Serializable
