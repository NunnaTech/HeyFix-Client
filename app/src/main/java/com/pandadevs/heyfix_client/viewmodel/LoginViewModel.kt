package com.pandadevs.heyfix_client.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandadevs.heyfix_client.provider.LoginProvider

class LoginViewModel:ViewModel() {
    var result: MutableLiveData<Boolean> = MutableLiveData()
    var error: MutableLiveData<Boolean> = MutableLiveData()

    suspend fun loginEmail(email:String,password:String){
        var response = LoginProvider.LoginWithEmail(email, password)
        if (response!!){
            result.postValue(true)
        }else{
            error.postValue(false)
        }
    }

}