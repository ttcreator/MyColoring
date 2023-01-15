package com.ttcreator.mycoloring;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ttcreator.mycoloring.menuItemFragment.Search;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationScheduler {

    private Context context;

    public NotificationScheduler(Context context) {
        this.context = context;
    }

    public void main() {

        TimerTask notificationTask = new TimerTask() {
            public void run() {
                sendNotifications();
                System.out.println("Отправка уведомления");
            }
        };

        Timer timer = new Timer();
        // Запланируем уведомление на каждый день в 10:00
        timer.schedule(notificationTask, getTomorrow10AM(), 24 * 60 * 60 * 1000);

    }

    private  Date getTomorrow10AM() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 32);
        return calendar.getTime();
    }

    private PendingIntent createNotificationIntent() {
        Intent pushIntent = new Intent(context, MainActivity.class);
        pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, pushIntent, PendingIntent.FLAG_IMMUTABLE);
        return pendingIntent;
    }


    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_notify);
            String description = context.getString(R.string.channel_notify_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("firstChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotifications () {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "firstChannel")
                .setSmallIcon(R.drawable.icon_bell_notify)
                .setContentTitle("Wow! You have free items!")
                .setContentText("You have 15 min for geting premium image free")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(createNotificationIntent())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(new Random().nextInt(), builder.build());
    }
}



