package com.orderfoodserver.teknomerkez.orderfoodserver.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.orderfoodserver.teknomerkez.orderfoodserver.R;

import static android.app.Notification.VISIBILITY_PRIVATE;

public class NotificationHelper extends ContextWrapper {

    private static final String OrderFood_Channel_ID = "com.orderfoodserver.teknomerkez.orderfoodserver.OrderFoodServer";
    private static final String OrderFood_Channel_NAME = "Order Food";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // this function is only working if API 26 or higher
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel orderFoodChannel = new NotificationChannel(OrderFood_Channel_ID, OrderFood_Channel_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        orderFoodChannel.enableLights(false);
        orderFoodChannel.enableVibration(true);
        orderFoodChannel.setLockscreenVisibility(VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(orderFoodChannel);

    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOrderFoodChannelNotification(String title, String body, PendingIntent content, Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), OrderFood_Channel_ID)
                .setContentIntent(content)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setSound(soundUri);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOrderFoodChannelNotification(String title, String body, Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), OrderFood_Channel_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setSound(soundUri);
    }
}
