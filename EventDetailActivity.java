package com.ujjawal.collegealert.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.ujjawal.collegealert.R;
import com.ujjawal.collegealert.database.DatabaseHelper;
import com.ujjawal.collegealert.models.Event;
import com.ujjawal.collegealert.notifications.NotificationHelper;

public class EventDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private Event          event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(v -> onBackPressed());

        db = DatabaseHelper.getInstance(this);
        int id = getIntent().getIntExtra("event_id", -1);
        if (id == -1) { finish(); return; }

        event = db.getEventById(id);
        if (event == null) { finish(); return; }

        bindViews();
    }

    private void bindViews() {
        ((TextView) findViewById(R.id.tvEmoji)).setText(event.getTypeEmoji());
        ((TextView) findViewById(R.id.tvType)).setText(event.getType());
        ((TextView) findViewById(R.id.tvTitle)).setText(event.getTitle());
        ((TextView) findViewById(R.id.tvDate)).setText("📅  " + event.getDate());
        ((TextView) findViewById(R.id.tvTime)).setText("⏰  " + event.getTime());
        ((TextView) findViewById(R.id.tvVenue)).setText("📍  " +
            (event.getVenue().isEmpty() ? "TBA" : event.getVenue()));
        ((TextView) findViewById(R.id.tvDescription)).setText(
            event.getDescription().isEmpty() ?
            "No description provided." : event.getDescription());
        ((MaterialCardView) findViewById(R.id.cardHeader))
            .setCardBackgroundColor(event.getTypeColor());

        TextView tvStatus = findViewById(R.id.tvStatus);
        MaterialButton btnNotify = findViewById(R.id.btnNotify);

        if (event.getDateTimeMillis() < System.currentTimeMillis()) {
            tvStatus.setText("✅ Event Completed");
            tvStatus.setBackgroundColor(0xFF388E3C);
            btnNotify.setVisibility(View.GONE);
        } else {
            tvStatus.setText("🔔 Upcoming Event");
            tvStatus.setBackgroundColor(0xFF1565C0);
        }

        ((MaterialButton) findViewById(R.id.btnDelete))
            .setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Remove \"" + event.getTitle() + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    NotificationHelper.cancelEventAlarm(this, event.getId());
                    db.deleteEvent(event.getId());
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null).show());

        btnNotify.setOnClickListener(v -> {
            NotificationHelper.createNotificationChannel(this);
            NotificationHelper.showEventNotification(this, event);
            Toast.makeText(this, "🔔 Notification sent!", Toast.LENGTH_SHORT).show();
        });
    }
}