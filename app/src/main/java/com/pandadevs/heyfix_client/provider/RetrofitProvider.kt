package com.pandadevs.heyfix_client.provider

import com.pandadevs.heyfix_client.data.model.NotificationModel
import com.pandadevs.heyfix_client.service.RetrofitService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitProvider {

    companion object {
        suspend fun sendNotification(data: NotificationModel) {
            val retrofit = Retrofit
                .Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(RetrofitService::class.java).sendNotification(
                "key=AAAADjUeEXA:APA91bEVmnJzQriACdsV8bYtDyX6IFmJZoWVdiTM1gsDw4Jy8ujDRL9jLMYLhqgIbk78j5UOwxhadNV4IylsDzVHNH3hCWOnFQNJt8rbj7Suo1zxEMoQZQmUcbL-n2rfkzoLeZuPkF5M",
                data
            )

        }
    }
}