package com.pandadevs.heyfix_client.provider

import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.data.model.HiredServiceModel
import com.pandadevs.heyfix_client.data.model.UserGet
import kotlinx.coroutines.CompletableDeferred

class HiredServiceProvider {

    companion object {

        fun deleteChatRealTime(id:String){
            FirebaseDatabase.getInstance()
                .getReference(id)
                .removeValue()
        }

        suspend fun getDataHiredService(id: String): HiredServiceModel {
            val response: CompletableDeferred<HiredServiceModel> =
                CompletableDeferred<HiredServiceModel>()
            FirebaseFirestore
                .getInstance()
                .collection("hired_service")
                .document(id)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val data = it.data
                        val hiredService = HiredServiceModel(
                            id = id,
                            client_ubication = data?.get("client_ubication") as GeoPoint,
                            client_name = data?.get("client_name").toString(),
                            worker_ubication = data["worker_ubication"] as GeoPoint,
                            worker_name = data?.get("worker_name").toString(),
                            date_hired = data?.get("date_hired") as Timestamp,
                            address = data["address"].toString(),
                            ranked = data["ranked"].toString().toInt(),
                            completed = data["ranked"].toString().toBoolean(),
                            canceled = data["canceled"].toString().toBoolean(),
                            arrived = data["arrived"].toString().toBoolean(),
                            review = data["review"].toString(),
                            client_id = data["client_id"].toString(),
                            worker_id = data["worker_id"].toString(),
                            category_id = data["category_id"].toString(),
                        )
                        response.complete(hiredService)
                    }
                }

            return response.await()
        }

        suspend fun getDataWorker(id: String): UserGet {
            val response: CompletableDeferred<UserGet> = CompletableDeferred<UserGet>()
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(id)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val data = it.data
                        val user = UserGet(
                            id = it.id,
                            name = data?.get("name").toString(),
                            first_surname = data?.get("first_surname").toString(),
                            second_surname = data?.get("second_surname").toString(),
                            active = data?.get("active").toString().toBoolean(),
                            client = data?.get("client").toString().toBoolean(),
                            email = data?.get("email").toString(),
                            phone_number = data?.get("phone_number").toString(),
                            picture = data?.get("picture").toString(),
                            ranked_avg = data?.get("ranked_avg").toString().toDouble(),
                            transport = data?.get("transport").toString(),
                            category_id = data?.get("category_id").toString(),
                            last_online = data?.get("last_online") as Timestamp,
                            current_position = data["current_position"] as GeoPoint,
                            tokenNotification = data["tokenNotification"].toString(),
                        )
                        response.complete(user)
                    }
                }

            return response.await()
        }

        suspend fun getDataCategory(id: String): CategoryModel {
            val response: CompletableDeferred<CategoryModel> = CompletableDeferred<CategoryModel>()
            FirebaseFirestore
                .getInstance()
                .collection("categories")
                .document(id)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val data = it.data
                        val category = CategoryModel(
                            id = it.id,
                            name = data?.get("name").toString(),
                            icon = data?.get("icon").toString(),
                            description = data?.get("description").toString(),
                        )
                        response.complete(category)
                    }
                }
            return response.await()
        }

        fun rateHiredService(id: String, rate: Int, review: String) {
            FirebaseFirestore
                .getInstance()
                .collection("hired_service")
                .document(id)
                .update(mapOf(
                    "ranked" to rate,
                    "review" to review,
                ))
        }

        suspend fun statusService(id: String, status: String): Boolean {
            val response = CompletableDeferred<Boolean>()
            FirebaseFirestore
                .getInstance()
                .collection("hired_service")
                .document(id)
                .update(status, true)
                .addOnSuccessListener { response.complete(true) }
                .addOnFailureListener { response.complete(false) }
            return response.await()
        }

        fun activeUsers(idClient: String, idWorker: String){
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(idClient)
                .update("active", true)
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(idWorker)
                .update("active", true)
        }
    }
}