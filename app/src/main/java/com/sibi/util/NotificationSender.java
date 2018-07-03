package com.sibi.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by adway on 08/12/17.
 */

public class NotificationSender {
    public static final String TAG = "NotificationSender";

    public static void sendNotification(Context context, String msg) {
        Log.d(TAG, "sendNotification: ");
        NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
//        Notification notification = new .Builder(context)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentText(msg)
//
//            .setChannelId(CHANNEL_ID)
//            .build();
//        nmc.notify(3, notification);
        Toast.makeText(context,
            "Broadcast received from Alarm Manager.", Toast.LENGTH_SHORT).show();
    }

    public static class MyBroadcastReceiver extends BroadcastReceiver {
        public static final String TAG = "MyBroadcastReceiver";

        public MyBroadcastReceiver() {
        }

        @Override public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            sendNotification(context, "Remember to record your expenses!");
        }
    }
}
