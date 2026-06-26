package com.ujjawal.collegealert.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;
import com.google.android.material.chip.*;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.ujjawal.collegealert.R;
import com.ujjawal.collegealert.adapters.EventAdapter;
import com.ujjawal.collegealert.database.DatabaseHelper;
import com.ujjawal.collegealert.models.Event;
import com.ujjawal.collegealert.notifications.NotificationHelper;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements EventAdapter.OnEventClickListener {

    private RecyclerView   recyclerView;
    private TextView       tvEmpty, tvEventCount;
    private ChipGroup      chipGroup;
    private DatabaseHelper db;
    private String         currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        db           = DatabaseHelper.getInstance(this);
        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty      = findViewById(R.id.tvEmpty);
        tvEventCount = findViewById(R.id.tvEventCount);
        chipGroup    = findViewById(R.id.chipGroup);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v ->
            startActivity(new Intent(this, AddEventActivity.class)));

        setupChips();
        loadEvents();
    }

    private void setupChips() {
        String[] filters = {"All", Event.TYPE_SEMINAR, Event.TYPE_EXAM,
                             Event.TYPE_FEST, Event.TYPE_NOTICE};
        for (String filter : filters) {
            Chip chip = new Chip(this);
            chip.setText(filter);
            chip.setCheckable(true);
            chip.setChecked(filter.equals(currentFilter));
            chip.setChipBackgroundColorResource(R.color.chip_bg_selector);
            chip.setTextColor(getResources().getColorStateList(
                R.color.chip_text_selector, getTheme()));
            chip.setOnCheckedChangeListener((btn, checked) -> {
                if (checked) { currentFilter = filter; loadEvents(); }
            });
            chipGroup.addView(chip);
        }
    }

    private void loadEvents() {
        List<Event> events = "All".equals(currentFilter)
            ? db.getAllEvents() : db.getEventsByType(currentFilter);

        tvEventCount.setText(events.size() + " event" +
            (events.size() == 1 ? "" : "s"));

        if (events.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setAdapter(new EventAdapter(events, this));
        }
        long now = System.currentTimeMillis();
        for (Event e : db.getAllEvents())
            if (e.getDateTimeMillis() > now && !e.isNotified())
                NotificationHelper.scheduleEventAlarm(this, e);
    }

    @Override protected void onResume() { super.onResume(); loadEvents(); }

    @Override
    public void onEventClick(Event event) {
        Intent i = new Intent(this, EventDetailActivity.class);
        i.putExtra("event_id", event.getId());
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("About College Alert")
                .setMessage("📱 College Alert\n\nDeveloper: Ujjawal Kumar Verma\n" +
                    "Roll No: SBU220455\n\n🎓 Seminars • 📝 Exams\n🎉 Fests • 📢 Notices")
                .setPositiveButton("OK", null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}