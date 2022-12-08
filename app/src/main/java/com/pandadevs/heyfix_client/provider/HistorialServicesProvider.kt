package com.pandadevs.heyfix_client.provider

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pandadevs.heyfix_client.data.model.HistorialServiceModel
import kotlinx.coroutines.CompletableDeferred

class HistorialServicesProvider {
    companion object {
        suspend fun getHistorialServices(idUser: String): List<HistorialServiceModel> {
            val response = CompletableDeferred<List<HistorialServiceModel>>()
            FirebaseFirestore
                .getInstance()
                .collection("hired_service")
                .whereEqualTo("client_id", idUser)
                .orderBy("date_hired", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    it.documents
                    val list = mutableListOf<HistorialServiceModel>()
                    for (document in it) {
                        val service = HistorialServiceModel(
                            worker_name = document["worker_name"].toString(),
                            category_name = document["category_name"].toString(),
                            address = document["address"].toString(),
                            canceled = document["canceled"].toString().toBoolean(),
                            completed = document["completed"].toString().toBoolean(),
                            date_hired = document["date_hired"] as Timestamp
                        )
                        list.add(service)
                    }
                    response.complete(list)
                }
                .addOnFailureListener { response.complete(listOf()) }
            return response.await()

        }
    }
}