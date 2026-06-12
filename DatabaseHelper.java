package com.ujjawal.collegealert.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.ujjawal.collegealert.models.Event;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "college_alert.db";
    private static final int    DB_VERSION = 1;

    public static final String TABLE  = "events";
    public static final String COL_ID = "id",      COL_TITLE = "title";
    public static final String COL_DESC = "description", COL_TYPE = "type";
    public static final String COL_DATE = "date",   COL_TIME  = "time";
    public static final String COL_VENUE = "venue", COL_MILLIS = "datetime_millis";
    public static final String COL_NOTIFIED = "notified";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) instance = new DatabaseHelper(ctx.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context ctx) { super(ctx, DB_NAME, null, DB_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + "(" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_TITLE + " TEXT NOT NULL," + COL_DESC + " TEXT," +
            COL_TYPE + " TEXT NOT NULL," + COL_DATE + " TEXT NOT NULL," +
            COL_TIME + " TEXT NOT NULL," + COL_VENUE + " TEXT," +
            COL_MILLIS + " INTEGER," + COL_NOTIFIED + " INTEGER DEFAULT 0)");
        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE); onCreate(db);
    }

    public long insertEvent(Event e) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE, null, toCV(e));
        db.close(); return id;
    }

    public List<Event> getAllEvents() {
        return query(null, null);
    }

    public List<Event> getEventsByType(String type) {
        return query(COL_TYPE + "=?", new String[]{type});
    }

    private List<Event> query(String where, String[] args) {
        List<Event> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, null, where, args, null, null, COL_MILLIS + " ASC");
        if (c.moveToFirst()) do { list.add(fromCursor(c)); } while (c.moveToNext());
        c.close(); db.close(); return list;
    }

    public Event getEventById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, null, COL_ID+"=?",
                new String[]{String.valueOf(id)}, null, null, null);
        Event e = c.moveToFirst() ? fromCursor(c) : null;
        c.close(); db.close(); return e;
    }

    public int updateEvent(Event e) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(TABLE, toCV(e), COL_ID+"=?",
                new String[]{String.valueOf(e.getId())});
        db.close(); return rows;
    }

    public void deleteEvent(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, COL_ID+"=?", new String[]{String.valueOf(id)});
        db.close();
    }

    private ContentValues toCV(Event e) {
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, e.getTitle());    cv.put(COL_DESC, e.getDescription());
        cv.put(COL_TYPE, e.getType());      cv.put(COL_DATE, e.getDate());
        cv.put(COL_TIME, e.getTime());      cv.put(COL_VENUE, e.getVenue());
        cv.put(COL_MILLIS, e.getDateTimeMillis());
        cv.put(COL_NOTIFIED, e.isNotified() ? 1 : 0);
        return cv;
    }

    private Event fromCursor(Cursor c) {
        Event e = new Event();
        e.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        e.setTitle(c.getString(c.getColumnIndexOrThrow(COL_TITLE)));
        e.setDescription(c.getString(c.getColumnIndexOrThrow(COL_DESC)));
        e.setType(c.getString(c.getColumnIndexOrThrow(COL_TYPE)));
        e.setDate(c.getString(c.getColumnIndexOrThrow(COL_DATE)));
        e.setTime(c.getString(c.getColumnIndexOrThrow(COL_TIME)));
        e.setVenue(c.getString(c.getColumnIndexOrThrow(COL_VENUE)));
        e.setDateTimeMillis(c.getLong(c.getColumnIndexOrThrow(COL_MILLIS)));
        e.setNotified(c.getInt(c.getColumnIndexOrThrow(COL_NOTIFIED)) == 1);
        return e;
    }

    private void seedData(SQLiteDatabase db) {
        Object[][] rows = {
            {"AI & Machine Learning Seminar",
             "Explore real-world AI/ML applications by industry experts. Open to all students.",
             Event.TYPE_SEMINAR, "25/06/2025", "10:00", "Seminar Hall A", 1750842600000L},
            {"Mid-Semester Exam – Mathematics",
             "Unit 1–3 syllabus. Bring admit card and college ID. No electronic devices.",
             Event.TYPE_EXAM, "28/06/2025", "09:00", "Exam Block C", 1751095200000L},
            {"Annual College Fest – TechVibe 2025",
             "Cultural performances, tech competitions, stalls, and celebrity night!",
             Event.TYPE_FEST, "05/07/2025", "11:00", "Main Ground", 1751698200000L},
            {"Library Clearance Notice",
             "All pending book returns must be completed by 30 June. Fine waivers till 25 June.",
             Event.TYPE_NOTICE, "20/06/2025", "08:00", "Central Library", 1750410600000L},
            {"Robotics Workshop – STEM Club",
             "Hands-on Arduino and sensor integration session. Limited seats.",
             Event.TYPE_SEMINAR, "30/06/2025", "14:00", "Lab 204", 1751268000000L},
            {"End-Term Exam – Physics",
             "Full syllabus exam. Practical viva the following day.",
             Event.TYPE_EXAM, "10/07/2025", "09:00", "Exam Block A", 1752130200000L},
            {"Sports Fest – Sprint 2025",
             "Inter-department athletics, cricket, and basketball. Register at gym office.",
             Event.TYPE_FEST, "15/07/2025", "08:00", "Sports Complex", 1752557400000L},
            {"Scholarship Application Deadline",
             "Submit merit scholarship forms with mark sheets to admin office by 3 PM.",
             Event.TYPE_NOTICE, "22/06/2025", "15:00", "Admin Office", 1750594200000L},
        };
        for (Object[] row : rows) {
            ContentValues cv = new ContentValues();
            cv.put(COL_TITLE, (String)row[0]); cv.put(COL_DESC, (String)row[1]);
            cv.put(COL_TYPE, (String)row[2]);  cv.put(COL_DATE, (String)row[3]);
            cv.put(COL_TIME, (String)row[4]);  cv.put(COL_VENUE, (String)row[5]);
            cv.put(COL_MILLIS, (long)row[6]);  cv.put(COL_NOTIFIED, 0);
            db.insert(TABLE, null, cv);
        }
    }
}