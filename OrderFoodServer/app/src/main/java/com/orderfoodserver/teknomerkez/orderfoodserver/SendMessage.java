package com.orderfoodserver.teknomerkez.orderfoodserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.orderfoodserver.teknomerkez.orderfoodserver.Common.Common;
import com.orderfoodserver.teknomerkez.orderfoodserver.Model.DataMessage;
import com.orderfoodserver.teknomerkez.orderfoodserver.Model.MyResponse;
import com.orderfoodserver.teknomerkez.orderfoodserver.Model.Token;
import com.orderfoodserver.teknomerkez.orderfoodserver.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {

    MaterialEditText edtCustomTitle, edtCustomMessage;
    FButton btnSendMessage;
    APIService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        edtCustomTitle = findViewById(R.id.edtCustomTitle);
        edtCustomMessage = findViewById(R.id.edtCustomMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        myService = Common.getFCMClient();


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create message
                String title = edtCustomTitle.getText().toString();
                String body = edtCustomMessage.getText().toString();
                RemoteMessage remoteMessage = new RemoteMessage.Builder("Notification").addData("title", title)
                        .addData("body", body)
                        .build();
                Map<String, String> dataSend = new HashMap<>();
                dataSend.put("title", title);
                dataSend.put("body", body);
                DataMessage dataMessage = new DataMessage(new Token().getToken(), dataSend);
                FirebaseMessaging.getInstance().send(remoteMessage);

                dataMessage.to = new StringBuilder("/topics/").append(Common.topicName).toString();
                Log.d("TO: ", dataMessage.toString());
                myService.sendNotification(dataMessage).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.code() == 200) {
                            if (response.isSuccessful()) {
                                Toast.makeText(SendMessage.this, "Message Sent", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(SendMessage.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(SendMessage.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getBaseContext(), Home.class);
        startActivity(intent);
    }
}
