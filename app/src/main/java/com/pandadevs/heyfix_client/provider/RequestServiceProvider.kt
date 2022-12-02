package com.pandadevs.heyfix_client.provider

import android.location.Location
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.pandadevs.heyfix_client.data.model.*
import kotlinx.coroutines.*

class RequestServiceProvider {
    companion object {

        private var numberOfRequest = 0
        private var numberOfUsers = 0


        fun deleteRequests(userId: String) {
            FirebaseFirestore
                .getInstance()
                .collection("request_service").whereEqualTo("client_id", userId).get()
                .addOnSuccessListener {
                    it.forEach { r -> r.reference.delete() }
                }
        }

        suspend fun searchRequestService(
            categoryModel: CategoryModel,
            client: MyGeocoder
        ): MutableList<UserSort> {
            val response = CompletableDeferred<MutableList<UserSort>>()
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .whereEqualTo("client", false)
                .whereEqualTo("category_id", categoryModel.id)
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener {
                    val users: List<UserGet> = it.map { u ->
                        UserGet(
                            active = u.data["active"].toString().toBoolean(),
                            category_id = u.data["category_id"].toString(),
                            client = u.data["client"].toString().toBoolean(),
                            current_position = u.data["current_position"] as? GeoPoint,
                            email = u.data["email"].toString(),
                            first_surname = u.data["first_surname"].toString(),
                            last_online = u.data["last_online"] as Timestamp,
                            name = u.data["name"].toString(),
                            phone_number = u.data["phone_number"].toString(),
                            picture = u.data["picture"].toString(),
                            ranked_avg = u.data["ranked_avg"].toString().toDouble(),
                            second_surname = u.data["second_surname"].toString(),
                            tokenNotification = u.data["tokenNotification"].toString(),
                            transport = u.data["transport"].toString(),
                            id = u.reference.id
                        )
                    }
                    response.complete(RequestServiceProvider().orderBestUser(users, client))
                }
                .addOnFailureListener {
                    response.complete(mutableListOf())
                }
            return response.await()
        }

        suspend fun waitForResponse(
            userId: String,
            myGeocoder: MyGeocoder,
            users: MutableList<UserSort>
        ): String {
            numberOfRequest = 0
            numberOfUsers = users.size
            val idRequestAccepted = CompletableDeferred<String>()
            for (user in users) {
                val idRequestService = CompletableDeferred<String>()
                FirebaseFirestore
                    .getInstance()
                    .collection("request_service")
                    .add(
                        hashMapOf(
                            "accepted" to "",
                            "address" to myGeocoder.address,
                            "client_id" to userId,
                            "worker_id" to user.user.id,
                        )
                    ).addOnSuccessListener { reference -> idRequestService.complete(reference.id) }

                RetrofitProvider.sendNotification(
                    NotificationModel(
                        to = user.user.tokenNotification,
                        data = hashMapOf(
                            "id" to idRequestService.await(),
                            "title" to "Nueva solicitud de servicio",
                            "address" to myGeocoder.address,
                            "client_id" to userId,
                            "worker_id" to user.user.id,
                        )
                    )
                )

                FirebaseFirestore
                    .getInstance()
                    .collection("request_service")
                    .document(idRequestService.await())
                    .addSnapshotListener { value, _ ->
                        if (value != null && value.exists()) {
                            if (value["accepted"].toString() != "") {
                                val isAccepted = value["accepted"].toString().toBoolean()
                                if (isAccepted) {
                                    idRequestAccepted.complete(value.id)
                                } else {
                                    FirebaseFirestore
                                        .getInstance()
                                        .collection("request_service")
                                        .document(value.id)
                                        .delete()
                                }
                                this@Companion.numberOfRequest += 1
                                if (this@Companion.numberOfRequest == this@Companion.numberOfUsers) {
                                    idRequestAccepted.complete("")
                                }


                            }
                        }
                    }
            }
            return idRequestAccepted.await()
        }
    }

    private fun orderBestUser(users: List<UserGet>, client: MyGeocoder): MutableList<UserSort> {
        val timeNow = Timestamp.now()
        val usersDisorder = mutableListOf<UserSort>()
        for (user in users) {
            usersDisorder.add(
                UserSort(
                    user = user,
                    minusTimeDifference = calculateTime(timeNow, user.last_online!!),
                    minusDistance = calculateDistance(client, user.current_position!!)
                )
            )
        }
        val usersOrder =
            usersDisorder.sortedWith(compareBy({ it.minusTimeDifference }, { it.minusDistance }))
        return usersOrder.toMutableList()
    }

    private fun calculateTime(now: Timestamp, user: Timestamp): Long {
        return now.seconds - user.seconds
    }

    private fun calculateDistance(client: MyGeocoder, worker: GeoPoint): Double {
        val locClient = Location("")
        locClient.latitude = client.latitude
        locClient.longitude = client.longitude
        val locWorker = Location("")
        locWorker.latitude = worker.latitude
        locWorker.longitude = worker.longitude
        return locClient.distanceTo(locWorker).toDouble()
    }
}

