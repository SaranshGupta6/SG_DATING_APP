package com.example.sgdatingapp.notification_model.api

import com.example.sgdatingapp.notification_model.PushNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @Headers("Content-Type: application/json", "Authorization : key=AAAA6wloju4:APA91bFOl3ayk-sYRmIyveQoUovEvSHpLRsYrq5h4Hah8GOD5KeyU6gAG-pfkbeyckr8-cdccs4al7KuiR-CdnR5pHdIOZAlf75H7vX39ba8B1guGw8I_93bhzeLbCxR0qaNyI15ujJq")

    @POST("fcm/send")
    fun sendNotification(@Body notification: PushNotification): Call<PushNotification>

}