package com.cs407.finalproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import java.util.Date;
import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// Activity that allows user to schedule a notification based on a selected date and time
public class setNotification extends AppCompatActivity {
    // TV Objects to update display
    TextView setDate;
    TextView setTime;
    // Check if time/date has been selected
    private boolean dateSelected;
    private boolean timeSelected;
    // Calendar objects
    final Calendar myCalendar = Calendar.getInstance();
    // Notification helper instance
    private NotificationHelper notifHelper = NotificationHelper.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_notif);
        setDate = findViewById(R.id.tvDate);
        setTime = findViewById(R.id.tvTime);
        dateSelected = false;
        timeSelected = false;
    }

    // Request permissions on launch
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
                if(!isGranted){
                    // Notify user
                    Toast.makeText(this, getApplicationContext().getString(R.string.please_allow), Toast.LENGTH_LONG).show();
                }
            });

    // Method to manually ask for permissions again
    private void requestPermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            return;
        }
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED){
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
    // Schedule notification according to "delay" or date
    private void scheduleNotification (Notification notification, long delay) {
        // Define intent
        Intent notificationIntent = new Intent( getApplicationContext(), PublishNotification.class);
        // Add information to intent for later use
        notificationIntent.putExtra(PublishNotification.NOTIFICATION_ID, notifHelper.notifID);
        notificationIntent.putExtra(PublishNotification.NOTIFICATION, notification);
        // Initialize pending intent w unique notification id
        PendingIntent pendingIntent = PendingIntent.getBroadcast ( this, notifHelper.notifID, notificationIntent, PendingIntent. FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
        // Intialize alarm manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Make sure alarmmanager intitialized correctly
        assert alarmManager != null;
        //alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP, delay, pendingIntent);
        // Set alarm for future notification
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }
    // Date picker listener
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);    // set year, month, & day
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateSelected = true;    // update status
            updateDate();   // update label
        }
    };
    // Shows datepicker UI
    public void setDate (View view) {
        new DatePickerDialog(
                setNotification.this, date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), // get year, month, and day
                myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
    // Time picker listener
    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);    // set hours, minutes
            myCalendar.set(Calendar.SECOND, 0);
            timeSelected = true;    // update status
            setTime.setText(hourOfDay + ":" + minute);  // update label
        }
    };
    // Shows timepicker UI
    public void setTime(View view){
        new TimePickerDialog(
                setNotification.this, time,
                myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),
                true
        ).show();
    }
    // MEthod to update textview to show selected date
    private void updateDate () {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        Date date = myCalendar.getTime();
        setDate.setText(sdf.format(date));
    }
    // On set notif button click
    public void clickFunc(View view){
        // If either date or time selected false
        if(!dateSelected || !timeSelected){
            Toast.makeText(setNotification.this, getApplicationContext().getString(R.string.please_select), Toast.LENGTH_LONG).show();
        }
        // CHeck if permissions are granted
        else if(!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            requestPermission();
        }
        // If everything true, create notif channel and schedule notification
        if(timeSelected && dateSelected && NotificationManagerCompat.from(this).areNotificationsEnabled()){
            notifHelper.createNotificationChannel(getApplicationContext());
            scheduleNotification(notifHelper.getNotification(getApplicationContext()), myCalendar.getTime().getTime());
            // Confirm notification
            Toast.makeText(setNotification.this, getApplicationContext().getString(R.string.confirmation), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);   // return to main
            startActivity(intent);
        }
    }
    // Move back to main activity
    public void backFunction(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}