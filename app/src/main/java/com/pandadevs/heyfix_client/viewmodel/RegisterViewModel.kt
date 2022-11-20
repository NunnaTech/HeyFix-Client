package com.pandadevs.heyfix_client.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandadevs.heyfix_client.data.model.UserPost
import com.pandadevs.heyfix_client.provider.RegisterProvider

class RegisterViewModel: ViewModel() {
    var result:MutableLiveData<String> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()

    suspend fun registerData(user:UserPost, password:String){
        val response = RegisterProvider.registerDataUser(user,password)
        if (response == "success"){
            result.postValue("Registro Exitoso")
        }else{
            error.postValue("El Registro de Datos fue Invalido")
        }
    }

}