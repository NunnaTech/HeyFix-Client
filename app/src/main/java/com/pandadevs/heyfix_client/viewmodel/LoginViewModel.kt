package com.pandadevs.heyfix_client.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandadevs.heyfix_client.provider.LoginProvider

class LoginViewModel:ViewModel() {
    var result: MutableLiveData<Boolean?> = MutableLiveData()
    var error: MutableLiveData<Boolean> = MutableLiveData()

    suspend fun loginEmail(email:String,password:String){
        val response = LoginProvider.loginWithEmail(email, password)
        if (response != null)  result.value = response
    }
}