package com.eslamshawky.hp.chatfirebae.Fragments;

import com.eslamshawky.hp.chatfirebae.Notifications.MyResponse;
import com.eslamshawky.hp.chatfirebae.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {"Content_Type:application/json",
                    "Authorization:key=AAAAAfDH510:APA91bGNI_YDtY-roqZI1w8X8bR81l8tGMklt_ZxYNphLrUzWPLxqsatIm6ZvjGO3cWeIz14Fm6UilQUSAqoisCeUzlRS37O1fHvkqyW_rJedNxhykN43FJPKXfYHAJQHaNSK9JPXZWo"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender sender);
}
