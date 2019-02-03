package com.orderfoodserver.teknomerkez.orderfoodserver.Remote;

import com.orderfoodserver.teknomerkez.orderfoodserver.Model.DataMessage;
import com.orderfoodserver.teknomerkez.orderfoodserver.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA2CfD5kM:APA91bFpclm6dQWZsFGK-Kxkrgq25wpa67jOiGmuI2eops0snnmsvcJeaW3X9LNoXwRPaBsMDFu1cDTt_DKriKWNJ3SKACpupVbprsoXRn5jSzeLc5QZp9umWZ0beqs1nV1GNK7tBwBe"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body DataMessage body);
}
