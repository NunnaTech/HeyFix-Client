package com.pandadevs.heyfix_client.data.model

import java.io.Serializable

data class CategoryModel(
    var id: String,
    var name: String,
    var icon: String,
    var description: String
) : Serializable