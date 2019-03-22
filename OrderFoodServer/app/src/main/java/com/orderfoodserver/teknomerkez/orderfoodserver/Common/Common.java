package com.orderfoodserver.teknomerkez.orderfoodserver.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.orderfoodserver.teknomerkez.orderfoodserver.Model.Request;
import com.orderfoodserver.teknomerkez.orderfoodserver.Remote.APIService;
import com.orderfoodserver.teknomerkez.orderfoodserver.Remote.FCMRetrofitClient;
import com.orderfoodserver.teknomerkez.orderfoodserver.Remote.IGeoCoordinates;
import com.orderfoodserver.teknomerkez.orderfoodserver.Remote.RetrofitClient;

public class Common {

    public static Request currentRequest;

    public static String topicName = "News";

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String SELECT_THE_ACTİON = "Select The Action";
    public static final String UPDATE_CATEGORY = "Update Category";
    public static final String ADD_NEW_FOOD = "Add New Food";
    public static final String ADD_NEW_BANNER = "Add New Banner";
    public static final String FOOD_UPDATED = "Food Updated!";
    public static final String BANNER_UPDATED = "Banner Updated!";
    public static final String EDİT_FOOD = "Edit Food";
    public static final String EDİT_BANNER = "Edit Banner";
    public static final String FİLL_İNFO = "Please fill full information";
    public static final String NEW_CATEGORY = "New Category ";
    public static final String NEW_FOOD = "New Food ";
    public static final String WAS_ADDED = " was added";
    public static final String MENU_MANAGEMENT = "Menu Management";
    public static final String CATEGORY = "Category";
    public static final String IMAGE_SELECTED = "image Selected!";
    public static final String UPDATE_ORDER = "Update Order!";
    public static final String CHOOSE_STATUS = "PLease Choose Status";
    public static final String PLACED = "Placed";
    public static final String SHİPPED = "On My Way";
    public static final String ON_MY_WAY = "Shipped";
    public static final String THİS_DEVİCE_DOESNT_SUPPORT = "This Device doesn't support";

    // intent
    public static final String GET_CATEGORY_ID = "CategoryId";
    public static final String GET_ORDER_ID = "OrderID";
    public static final String GET_USER_NAME = "userName";

    public static final int PICK_IMAGE_REQUEST = 71;
    public static final int PLAY_SERVİCES_RESOLUTİON_REQUEST = 1000;
    public static final int LOCATİON_PERMİSSİON_REQUEST = 1001;

    public static final String baseUrl = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";

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

    public static String convertCodeToStatus (String code){
        if (code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On My Way";
        else
            return "Shipped";
    }

    public static IGeoCoordinates getGeoCodeServices(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static APIService getFCMClient(){
        return FCMRetrofitClient.getClient(fcmURL).create(APIService.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleX = newWidth/(float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

}
