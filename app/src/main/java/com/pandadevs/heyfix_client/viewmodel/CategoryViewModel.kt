package com.pandadevs.heyfix_client.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandadevs.heyfix_client.data.model.CategoryModel
import com.pandadevs.heyfix_client.provider.CategoryProvider

class CategoryViewModel : ViewModel() {

    var result: MutableLiveData<List<CategoryModel>> = MutableLiveData()
    var error: MutableLiveData<Boolean> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    suspend fun getAllCategories() {
        isLoading.value = true
        val response = CategoryProvider.getAllCategories()
        result.value = response
        error.value = response.isEmpty()
        isLoading.value = false
    }

}