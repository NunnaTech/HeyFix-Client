package com.pandadevs.heyfix_client.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.data.model.HiredServiceModel
import com.pandadevs.heyfix_client.data.model.UserGet
import com.pandadevs.heyfix_client.provider.HiredServiceProvider

class HiredServiceViewModel : ViewModel() {

    var dataCategory = MutableLiveData<CategoryModel>()
    var dataWorker = MutableLiveData<UserGet>()
    var cancelService = MutableLiveData<Boolean>()
    var isThereACurrentService: MutableLiveData<HiredServiceModel?> = MutableLiveData<HiredServiceModel?>()
    var isThereACurrentServiceBoolean: MutableLiveData<HiredServiceModel?> = MutableLiveData<HiredServiceModel?>()

    fun rateHiredService(id: String, ranked:Int, review:String) {
        HiredServiceProvider.rateHiredService(id, ranked, review)
    }

    suspend fun getDataHiredService(id: String) {
        val response = HiredServiceProvider.getDataHiredService(id)
        getDataWorker(response.worker_id)
        getDataCategory(response.category_id)
    }

    suspend fun getDataCategory(id: String) {
        val response = HiredServiceProvider.getDataCategory(id)
        dataCategory.value = response
    }

    suspend fun getDataWorker(id: String) {
        val response = HiredServiceProvider.getDataWorker(id)
        dataWorker.value = response
    }

    suspend fun cancelService(id: String, idClient: String, idWorker: String) {
        val response = HiredServiceProvider.statusService(id, "canceled")
        HiredServiceProvider.activeUsers(idClient, idWorker)
        HiredServiceProvider.deleteChatRealTime(id)
        cancelService.value = response
    }

    fun isThereACurrentService(id: String) {
        FirebaseFirestore
            .getInstance()
            .collection("hired_service")
            .document(id)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshots != null && snapshots.exists()) {
                    val data = snapshots.data
                    val hiredService = HiredServiceModel(
                        id = snapshots.id,
                        client_ubication = data?.get("client_ubication") as GeoPoint,
                        client_name = data["client_name"].toString(),
                        worker_ubication = data["worker_ubication"] as GeoPoint,
                        worker_name = data["worker_name"].toString(),
                        date_hired = data["date_hired"] as Timestamp,
                        address = data["address"].toString(),
                        ranked = data["ranked"].toString().toInt(),
                        completed = data["completed"].toString().toBoolean(),
                        canceled = data["canceled"].toString().toBoolean(),
                        arrived = data["arrived"].toString().toBoolean(),
                        review = data["review"].toString(),
                        client_id = data["client_id"].toString(),
                        worker_id = data["worker_id"].toString(),
                        category_id = data["category_id"].toString(),
                    )
                    isThereACurrentService.value = hiredService

                } else {
                    isThereACurrentService.value = null
                }
            }
    }

    fun isThereACurrentServiceBoolean(idUser: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("hired_service")
            .whereEqualTo("client_id", idUser)
            .whereEqualTo("completed", false)
            .whereEqualTo("canceled", false)
        docRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshots != null) {
                if (snapshots.documents.size > 0) {
                    val data = snapshots.documents[0].data
                    val hiredService = HiredServiceModel(
                        id = snapshots.documents[0].id,
                        client_ubication = data?.get("client_ubication") as GeoPoint,
                        client_name = data["client_name"].toString(),
                        worker_ubication = data["worker_ubication"] as GeoPoint,
                        worker_name = data["worker_name"].toString(),
                        date_hired = data["date_hired"] as Timestamp,
                        address = data["address"].toString(),
                        ranked = data["ranked"].toString().toInt(),
                        completed = data["completed"].toString().toBoolean(),
                        canceled = data["canceled"].toString().toBoolean(),
                        arrived = data["arrived"].toString().toBoolean(),
                        review = data["review"].toString(),
                        client_id = data["client_id"].toString(),
                        worker_id = data["worker_id"].toString(),
                        category_id = data["category_id"].toString(),
                    )
                    isThereACurrentServiceBoolean.value = hiredService
                } else {
                    isThereACurrentServiceBoolean.value = null
                }
            } else {
                isThereACurrentServiceBoolean.value = null
            }
        }
    }
}