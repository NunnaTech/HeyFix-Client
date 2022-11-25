package com.pandadevs.heyfix_client.provider

import com.google.firebase.firestore.FirebaseFirestore
import com.pandadevs.heyfix_client.data.model.CategoryModel
import kotlinx.coroutines.CompletableDeferred

class CategoryProvider {

    companion object {
        suspend fun getAllCategories(): List<CategoryModel> {
            val response = CompletableDeferred<List<CategoryModel>>()
            FirebaseFirestore.getInstance().collection("categories").get()
                .addOnSuccessListener {
                    val categories: List<CategoryModel> = it.map { c ->
                        CategoryModel(
                            id = c.reference.id,
                            name = c.data["name"].toString(),
                            icon = c.data["icon"].toString(),
                            description = c.data["description"].toString()
                        )
                    }
                    response.complete(categories)
                }
                .addOnFailureListener { response.complete(emptyList()) }
            return response.await()
        }
    }
}