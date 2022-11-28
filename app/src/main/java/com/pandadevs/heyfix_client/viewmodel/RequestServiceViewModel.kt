package com.pandadevs.heyfix_client.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.data.model.MyGeocoder
import com.pandadevs.heyfix_client.data.model.UserSort
import com.pandadevs.heyfix_client.provider.RequestServiceProvider


class RequestServiceViewModel : ViewModel() {

    var usersFound: MutableLiveData<MutableList<UserSort>> = MutableLiveData()
    var result: MutableLiveData<String> = MutableLiveData()

    suspend fun searchRequestService(
        categoryModel: CategoryModel,
        client: MyGeocoder
    ) {
        val result: MutableList<UserSort> =
            RequestServiceProvider.searchRequestService(categoryModel, client)
        usersFound.value = result
    }

    suspend fun waitForResponse(
        userId: String,
        myGeocoder: MyGeocoder,
        users: MutableList<UserSort>
    ) {
        val requestId = RequestServiceProvider.waitForResponse(userId, myGeocoder, users)
        result.value = requestId
    }

}