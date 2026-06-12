package com.ujjawal.collegealert.notifications;

import android.app.*;
import android.content.*;
import android.os.Build;
import androidx.core.app.*;
import com.ujjawal.collegealert.R;
import com.ujjawal.collegealert.activities.EventDetailActivity;
import com.ujjawal.collegealert.models.Event;

public class NotificationHelper {

    public static final String CHANNEL_ID = "college_alert_channel";

    public static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, "College Alert", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Campus event alerts");
            ch.enableVibration(true);
            ctx.getSystemService(NotificationManager.class).createNotificationChannel(ch);
        }
    }

    public static void showEventNotification(Context ctx, Event event) {
        Intent intent = new Intent(ctx, EventDetailActivity.class);
        intent.putExtra("event_id", event.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(ctx, event.getId(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(event.getTypeEmoji() + " " + event.getType() + " Alert!")
            .setContentText(event.getTitle() + " — " + event.getDate() + " at " + event.getTime())
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(event.getTitle() + "\n" + event.getDate() +
                         " at " + event.getTime() + "\nVenue: " + event.getVenue()))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi).setAutoCancel(true)
            .setColor(event.getTypeColor()).setColorized(true);

        NotificationManagerCompat.from(ctx).notify(event.getId(), b.build());
    }

    public static void scheduleEventAlarm(Context ctx, Event event) {
        long triggerTime = event.getDateTimeMillis() - (30 * 60 * 1000);
        if (triggerTime <= System.currentTimeMillis()) return;

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        intent.putExtra("event_id", event.getId());

        PendingIntent pi = PendingIntent.getBroadcast(ctx, event.getId(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi);
        else
            am.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pi);
    }

    public static void cancelEventAlarm(Context ctx, int eventId) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(ctx, eventId,
            new Intent(ctx, AlarmReceiver.class),
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.cancel(pi);
    }
}