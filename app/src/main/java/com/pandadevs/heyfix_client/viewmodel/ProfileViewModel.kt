package com.pandadevs.heyfix_client.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandadevs.heyfix_client.data.model.UserGet
import com.pandadevs.heyfix_client.provider.ProfileProvider
import com.pandadevs.heyfix_client.utils.datatype.ResultType

class ProfileViewModel: ViewModel()  {
    var result: MutableLiveData<String> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()
    var isDataProgress: MutableLiveData<Boolean> = MutableLiveData()

    fun updateUserData(user: UserGet){
        isDataProgress.value = true
        val response = ProfileProvider.updateUserData(user)
        if(response.resultType == ResultType.SUCCESS){
            result.postValue(response.data!!)
            isDataProgress.value = false
        }else{
            error.postValue(response.data!!)
            isDataProgress.value = false
        }
    }
    fun uploadUserImage(imageUri: Uri){
        val response = ProfileProvider.uploadUserImage(imageUri)
        if(response.resultType == ResultType.SUCCESS){
        }else{
            error.postValue(response.data!!)
        }
    }
}