package com.cs407.finalproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Class to publish notification once alarm has been received
public class PublishNotification extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification_id" ;
    public static String NOTIFICATION = "notification" ;
    // On reception of pending intent/alarm
    public void onReceive (Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            // Set importance
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(notification.getChannelId(), "NOTIFICATION_CHANNEL_NAME", importance);
            // Check that manager is init correctly
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        // get id from intent
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        // Check that manager is init correctly
        assert notificationManager != null;
        // send notif
        notificationManager.notify(id, notification);
    }
}
