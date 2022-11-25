package com.pandadevs.heyfix_client.data.model

import java.io.Serializable

data class MyGeocoder(
    var address: String,
    var latitude: Double,
    var longitude: Double
) : Serializable