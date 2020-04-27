package model;

import java.util.Date;

public class CalendarEvent {

    private String eventTitle;
    private Date eventDate;
    private Date startTime, endTime;
    private String eventLocation;
    private String eventNotes;
    private String eventId;

    public CalendarEvent(String title, Date date, Date start, Date end, String id) {
        this.eventTitle = title;
        this.eventDate = date;
        this.startTime = start;
        this.endTime = end;
        this.eventId = id;
        this.eventLocation = "";
        this.eventNotes = "";
    }

    public String getTitle() {
        return this.eventTitle;
    }

    public void setTitle(String title) {
        this.eventTitle = title;
    }

    public Date getDate() {
        return this.eventDate;
    }

    public void setDate(Date date) {
        this.eventDate = date;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Date start) {
        this.startTime = start;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date end) {
        this.endTime = end;
    }

    public String getLocation() {
        return this.eventLocation;
    }

    public void setLocation(String location) {
        this.eventLocation = location;
    }

    public String getNotes() {
        return this.eventNotes;
    }

    public void setNotes(String notes) {
        this.eventNotes = notes;
    }

    public String getEventId() {
        return this.eventId;
    }

}
