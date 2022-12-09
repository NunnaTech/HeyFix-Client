package com.pandadevs.heyfix_client.service

import com.pandadevs.heyfix_client.data.model.NotificationModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface RetrofitService {

    @POST("send")
    suspend fun sendNotification(
        @Header("Authorization") token: String,
        @Body data: NotificationModel
    ): Response<Void>
}