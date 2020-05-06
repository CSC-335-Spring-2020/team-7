package model;

import java.util.Date;
/**
 * This class represents an event that will be stored and displayed by
 * the calendar.
 * @author Amin Sennour
 */
public class CalendarEvent {

    private String eventTitle;
    private Date eventDate;
    private Date startTime, endTime;
    private String eventLocation;
    private String eventNotes;
    private String eventId;
    /**
     * Constructs a new Event object
     * @param title title of the event
     * @param date Date object with the current date, zero out the time
     * @param start Date object with the start time/day
     * @param end Date object with the end time/day
     * @param id unique id string
     */
    public CalendarEvent(String title, Date date, Date start, Date end, String id) {
        this.eventTitle = title;
        this.eventDate = date;
        this.startTime = start;
        this.endTime = end;
        this.eventId = id;
        this.eventLocation = "";
        this.eventNotes = "";
    }

    /**
     * Getter for the title
     * @return Title of the event
     */
    public String getTitle() {
        return this.eventTitle;
    }

    /**
     * Setter for the tile
     * @param title title of the event
     */
    public void setTitle(String title) {
        this.eventTitle = title;
    }
    /**
     * Getter for the date
     * @return Date of the event
     */
    public Date getDate() {
        return this.eventDate;
    }
    /**
     * Setter for the date
     * @param date of the event
     */
    public void setDate(Date date) {
        this.eventDate = date;
    }
    /**
     * Getter for the start time
     * @return startTime Date of the event
     */
    public Date getStartTime() {
        return this.startTime;
    }
    /**
     * Setter for the date
     * @param start start date of the event
     */
    public void setStartTime(Date start) {
        this.startTime = start;
    }

    /**
     * Getter for the endTime date object
     * @return endTime date object
     */
    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Date end) {
        this.endTime = end;
    }
    /**
     * Getter for the location of the event
     * @return event location
     */
    public String getLocation() {
        return this.eventLocation;
    }

    public void setLocation(String location) {
        this.eventLocation = location;
    }
    /**
     * Getter for the notes of an event
     * @return eventNotes
     */
    public String getNotes() {
        return this.eventNotes;
    }

    /**
     * Add a note to be saved in the event object
     * @param notes a string, can be whatever you want to save
     */
    public void setNotes(String notes) {
        this.eventNotes = notes;
    }
    /**
     * getter for the event id
     * @return eventID
     */
    public String getEventId() {
        return this.eventId;
    }

    /**
     * Exists to be overridden by subclasses, by default return's "NONE"
     * @return the string "NONE" to indicate it has no interval
     */
    public String getInterval(){
        return "NONE";
    }

}
