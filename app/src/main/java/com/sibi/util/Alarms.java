package com.sibi.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.AlarmManagerCompat;
import android.util.Log;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by adway on 08/12/17.
 */

public class Alarms {
    public static final String TAG = "Alarms";
    public static void createAlarmForNotification(Context context, long alarmTime) {
        Log.d(TAG, "createAlarmForNotification: ");
        Intent intent = new Intent(context, NotificationSender.MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
            0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }
}