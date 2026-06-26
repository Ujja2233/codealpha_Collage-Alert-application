package com.ujjawal.collegealert.notifications;

import android.app.*;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.*;
import com.ujjawal.collegealert.R;
import com.ujjawal.collegealert.activities.MainActivity;
import android.app.PendingIntent;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage msg) {
        NotificationHelper.createNotificationChannel(this);
        String title = "College Alert", body = "New campus update!";
        if (msg.getNotification() != null) {
            title = msg.getNotification().getTitle();
            body  = msg.getNotification().getBody();
        } else if (!msg.getData().isEmpty()) {
            title = msg.getData().getOrDefault("title", title);
            body  = msg.getData().getOrDefault("body",  body);
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title).setContentText(body)
            .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi);

        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE))
            .notify((int)System.currentTimeMillis(), b.build());
    }

    @Override
    public void onNewToken(String token) { /* Send to server */ }
}