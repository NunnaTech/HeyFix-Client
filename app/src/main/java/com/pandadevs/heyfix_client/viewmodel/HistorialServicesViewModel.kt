package com.pandadevs.heyfix_client.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandadevs.heyfix_client.data.model.HistorialServiceModel
import com.pandadevs.heyfix_client.provider.HistorialServicesProvider

class HistorialServicesViewModel : ViewModel() {

    var historialServices = MutableLiveData<List<HistorialServiceModel>>()

    suspend fun getHistorialServices(idUser: String) {
        val response = HistorialServicesProvider.getHistorialServices(idUser)
        historialServices.value = response

    }
}