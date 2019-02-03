package com.orderfood.teknomerkez.orderfood.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.orderfood.teknomerkez.orderfood.Model.Request;
import com.orderfood.teknomerkez.orderfood.Remote.APIService;
import com.orderfood.teknomerkez.orderfood.Remote.RetrofitClient;

public class Common {

    public static Request commonRequest;

    public static String topicName = "News";
    public static String DELETE = "Delete";
    public static String BASE_URL = "https://fcm.googleapis.com/";
    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
    public static String convertCarttoStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On My Way";
        else
            return "Shipped";
    }

    public static boolean isConnectedtoInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){
                for (int i=0; i<info.length;i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED  ){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //intent
    public static String GET_USER_NAME = "userName";
    public static String GO_TO_COMMENTS = "Comments";

}
