package com.cs407.finalproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    // Construcrtor
    private static final NotificationHelper INSTANCE = new NotificationHelper();

    private NotificationHelper(){}
    // Accessor
    public static NotificationHelper getInstance(){
        return INSTANCE;
    }
    // Channel ID declaration
    public static final String CHANNEL_ID = "channel_chat";

        // Create notification channel
    public void createNotificationChannel(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    // Notification fields
    public int notifID = 0;
    public String title = null;
    public String reminder = null;

        // Set notification fiflds
    public void setNotificationContent(String title, String reminder){
        this.title = title;
        this.reminder=reminder;
        this.notifID++;
    }
    // Method to return a create notification using string.xml values
    public Notification getNotification(Context context){
        this.setNotificationContent(context.getString(R.string.title), context.getString(R.string.title));
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(reminder) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground);
        builder.setAutoCancel(true);
        builder.setChannelId(CHANNEL_ID);
        return builder.build();
    }
}
