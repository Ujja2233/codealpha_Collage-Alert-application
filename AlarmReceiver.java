package com.ujjawal.collegealert.notifications;

import android.content.*;
import com.ujjawal.collegealert.database.DatabaseHelper;
import com.ujjawal.collegealert.models.Event;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        int eventId = intent.getIntExtra("event_id", -1);
        if (eventId == -1) return;
        DatabaseHelper db = DatabaseHelper.getInstance(ctx);
        Event event = db.getEventById(eventId);
        if (event != null) {
            NotificationHelper.createNotificationChannel(ctx);
            NotificationHelper.showEventNotification(ctx, event);
            event.setNotified(true);
            db.updateEvent(event);
        }
    }
}