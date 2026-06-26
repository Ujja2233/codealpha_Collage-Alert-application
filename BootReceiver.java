package com.ujjawal.collegealert.notifications;

import android.content.*;
import com.ujjawal.collegealert.database.DatabaseHelper;
import com.ujjawal.collegealert.models.Event;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;
        NotificationHelper.createNotificationChannel(ctx);
        List<Event> events = DatabaseHelper.getInstance(ctx).getAllEvents();
        long now = System.currentTimeMillis();
        for (Event e : events)
            if (e.getDateTimeMillis() > now && !e.isNotified())
                NotificationHelper.scheduleEventAlarm(ctx, e);
    }
}