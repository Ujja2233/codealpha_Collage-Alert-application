package com.ujjawal.collegealert.models;

public class Event {

    public static final String TYPE_SEMINAR = "Seminar";
    public static final String TYPE_EXAM    = "Exam";
    public static final String TYPE_FEST    = "Fest";
    public static final String TYPE_NOTICE  = "Important Notice";

    private int     id;
    private String  title, description, type, date, time, venue;
    private long    dateTimeMillis;
    private boolean notified;

    public Event() {}

    public Event(String title, String description, String type,
                 String date, String time, String venue, long dateTimeMillis) {
        this.title = title; this.description = description; this.type = type;
        this.date = date;   this.time = time;               this.venue = venue;
        this.dateTimeMillis = dateTimeMillis; this.notified = false;
    }

    // Getters & Setters
    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }
    public String getTitle()                { return title; }
    public void setTitle(String t)          { this.title = t; }
    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }
    public String getType()                 { return type; }
    public void setType(String t)           { this.type = t; }
    public String getDate()                 { return date; }
    public void setDate(String d)           { this.date = d; }
    public String getTime()                 { return time; }
    public void setTime(String t)           { this.time = t; }
    public String getVenue()                { return venue; }
    public void setVenue(String v)          { this.venue = v; }
    public long getDateTimeMillis()         { return dateTimeMillis; }
    public void setDateTimeMillis(long m)   { this.dateTimeMillis = m; }
    public boolean isNotified()             { return notified; }
    public void setNotified(boolean n)      { this.notified = n; }

    public int getTypeColor() {
        switch (type) {
            case TYPE_SEMINAR: return 0xFF1565C0;
            case TYPE_EXAM:    return 0xFFC62828;
            case TYPE_FEST:    return 0xFF2E7D32;
            case TYPE_NOTICE:  return 0xFFE65100;
            default:           return 0xFF37474F;
        }
    }

    public String getTypeEmoji() {
        switch (type) {
            case TYPE_SEMINAR: return "🎓";
            case TYPE_EXAM:    return "📝";
            case TYPE_FEST:    return "🎉";
            case TYPE_NOTICE:  return "📢";
            default:           return "📌";
        }
    }
}