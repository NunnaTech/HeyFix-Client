package com.pandadevs.heyfix_client.provider

import android.location.Location
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.data.model.MyGeocoder
import com.pandadevs.heyfix_client.data.model.UserGet
import com.pandadevs.heyfix_client.data.model.UserSort
import kotlinx.coroutines.CompletableDeferred

class RequestServiceProvider {
    companion object {
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
                            last_online = u.data["last_online"] as? Timestamp,
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
                    val result = RequestServiceProvider().orderBestUser(users, client)
                    response.complete(result)
                }
                .addOnFailureListener {
                    response.complete(mutableListOf())
                    Log.e("Error", it.message.toString())
                }
            return response.await()
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

