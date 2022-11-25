package com.pandadevs.heyfix_client.data.model

data class UserSort(
    var user: UserGet,
    var minusTimeDifference: Long,
    var minusDistance: Double
)