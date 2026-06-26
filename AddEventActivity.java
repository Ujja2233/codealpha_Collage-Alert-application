package com.ujjawal.collegealert.activities;

import android.app.*;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ujjawal.collegealert.R;
import com.ujjawal.collegealert.database.DatabaseHelper;
import com.ujjawal.collegealert.models.Event;
import com.ujjawal.collegealert.notifications.NotificationHelper;
import java.text.*;
import java.util.*;

public class AddEventActivity extends AppCompatActivity {

    private TextInputEditText    etTitle, etDescription, etDate, etTime, etVenue;
    private AutoCompleteTextView spinnerType;
    private final Calendar       cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Event");
        }
        tb.setNavigationOnClickListener(v -> onBackPressed());

        etTitle       = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDate        = findViewById(R.id.etDate);
        etTime        = findViewById(R.id.etTime);
        etVenue       = findViewById(R.id.etVenue);
        spinnerType   = findViewById(R.id.spinnerType);

        String[] types = {Event.TYPE_SEMINAR, Event.TYPE_EXAM,
                          Event.TYPE_FEST, Event.TYPE_NOTICE};
        spinnerType.setAdapter(new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, types));
        spinnerType.setText(Event.TYPE_SEMINAR, false);

        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> new DatePickerDialog(this,
            (vw, y, m, d) -> {
                cal.set(y, m, d);
                etDate.setText(String.format(Locale.getDefault(),
                    "%02d/%02d/%04d", d, m+1, y));
            }, cal.get(Calendar.YEAR),
               cal.get(Calendar.MONTH),
               cal.get(Calendar.DAY_OF_MONTH))
            {{ getDatePicker().setMinDate(System.currentTimeMillis()); }}
            .show());

        etTime.setFocusable(false);
        etTime.setOnClickListener(v -> new TimePickerDialog(this,
            (vw, h, min) -> {
                cal.set(Calendar.HOUR_OF_DAY, h);
                cal.set(Calendar.MINUTE, min);
                etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, min));
            }, cal.get(Calendar.HOUR_OF_DAY),
               cal.get(Calendar.MINUTE), true).show());

        ((MaterialButton) findViewById(R.id.btnSave))
            .setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String title = str(etTitle), desc = str(etDescription);
        String date  = str(etDate),  time = str(etTime);
        String venue = str(etVenue), type = spinnerType.getText().toString().trim();

        if (TextUtils.isEmpty(title)) { etTitle.setError("Required"); return; }
        if (TextUtils.isEmpty(date))  { etDate.setError("Required");  return; }
        if (TextUtils.isEmpty(time))  { etTime.setError("Required");  return; }

        long millis = 0;
        try {
            Date d = new SimpleDateFormat("dd/MM/yyyy HH:mm",
                Locale.getDefault()).parse(date + " " + time);
            if (d != null) millis = d.getTime();
        } catch (ParseException e) { e.printStackTrace(); }

        Event event = new Event(title, desc, type, date, time, venue, millis);
        long id = DatabaseHelper.getInstance(this).insertEvent(event);

        if (id != -1) {
            event.setId((int) id);
            if (millis > System.currentTimeMillis())
                NotificationHelper.scheduleEventAlarm(this, event);
            Toast.makeText(this, "✅ Event saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "❌ Save failed", Toast.LENGTH_SHORT).show();
        }
    }

    private String str(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}