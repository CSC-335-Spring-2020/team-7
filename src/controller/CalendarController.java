package controller;

import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.CalendarModel;
import model.CalendarRecurringEvent;

import java.lang.reflect.Array;
import java.util.*;

/**
 * This is the controller for the Calender App.
 *
 * Purpose : To take data from the model, organize it, and send it to the view,
 * as well as taking data from the view, and adding it to the model.
 *
 * @author Amin Sennour
 */

public class CalendarController {
    CalendarModel model;

    public CalendarController(CalendarModel m){
        model = m;
    }

    /**
     * Getter for the calender name
     * @return the name
     */
    public String getName(){
        return model.getName();
    }

    /**
     * Setter for the calender name
     * @param name the new name
     */
    public void setName(String name){
        model.setName(name);
    }

    /**
     * Getter for the calendar color
     * @return the color
     */
    public Color getColor(){
        return model.getColor();
    }

    /**
     * Setter for the calendar color
     * @param c the new color
     */
    public void setColor(Color c){
        model.setColor(c);
    }

    /**
     * The range is [startDate, endDate) IE, it won't include the endDate
     *
     * @param startDate the date at which the range should should, inclusive
     * @param endDate the date at which the range should end, exclusive
     * @return a map of dates to lists of calenderEvents corresponding to the
     * events happening between startDate and endDate.
     */
    public Map<Date, List<CalendarEvent>> getEvents(Date startDate, Date endDate){
        Map<Date, List<CalendarEvent>> ret = new HashMap<>();
        // make sure that the endDate isn't after the start date
        if(endDate.before(startDate)){
            throw new IllegalArgumentException("StartDate=" + startDate + " is after EndDate=" + endDate);
        }

        Date current = (Date) startDate.clone();
        while(current.before(endDate)){
            System.out.println(current);

            // cloning the list of that the view editing what's returned from this
            // method doesn't change the model
            List<CalendarEvent> toAdd = new ArrayList<>(model.getEvents(current));

            /*
             * Put some logic here to determine if there are any reoccurring
             * events happening on this day.
             */

            ret.put(current, toAdd);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(current);
            calendar.add(Calendar.DATE, 1);
            current = calendar.getTime();
        }
        return ret;
    }

    /**
     * Gets the events happening on a single day
     *
     * @param day the day to fetch
     * @return a list of th events happening on that day
     */
    public List<CalendarEvent> getEventsOnDay(Date day){
        // cloning the list of that the view editing what's returned from this
        // method doesn't change the model

        day.setHours(0);
        day.setMinutes(0);
        day.setSeconds(0);

        List<CalendarEvent> events = model.getEvents(day);
        if(events != null){
            return new ArrayList<>(events);
        }else{
            return null;
        }
    }

    /**
     * Takes the information needed to create an event, creates it, and adds it
     * to the calender model
     *
     * @param title title of the event
     * @param date date on which the event is taking place
     * @param startTime the time which the event starts on
     * @param endTime the time which the event ends on
     * @param location optional, pass empty string if not wanted, the location the
     *                 event should take place
     * @param notes optional, pass empty string if not wanted, any notes attached
     *              to the event
     */
    public void addEvent(String title, Date date, Date startTime, Date endTime, String location, String notes){
        String uuid = UUID.randomUUID().toString();

        // set the time of the "day" of the event to 0 so that the model doesn't
        // have to deal with makings May 5 12pm and May 5 11am the same slot
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        CalendarEvent event = new CalendarEvent(title, date, startTime, endTime, uuid);

        event.setLocation(location);
        event.setNotes(notes);
        model.addEvent(date, event);
    }

    public void addRecurringEvent(String title, Date date, Date startTime, Date endTime, String location, String notes, int interval){
        String uuid = UUID.randomUUID().toString();

        // set the time of the "day" of the event to 0 so that the model doesn't
        // have to deal with makings May 5 12pm and May 5 11am the same slot
        date.setHours(12); // set these to take place at noon every "date" so they can be searched for easily
        date.setMinutes(0);
        date.setSeconds(0);

        CalendarRecurringEvent event = new CalendarRecurringEvent(title, date, startTime, endTime, uuid, interval);

        event.setLocation(location);
        event.setNotes(notes);
        model.addRecurringEvent(event);
    }

    /**
     * Used to remove an event when the user has access to the CalenderEvent
     * object
     *
     * @param toRemove event to remove
     */
    public void removeEvent(CalendarEvent toRemove){
        model.removeEvent(toRemove.getDate(), toRemove);
    }

    /**
     * Used to remove an event when the user has access to the CalenderEvent UUID
     *
     * @param uuid the id of the event to remove
     */
    public void removeEvent(String uuid){
        model.removeEvent(uuid);
    }

    /**
     * Gets the name of the calender when the controller is printed
     * @return to name of the calender
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Used to check if two calendars have the same name
     * @param o the calender
     * @return t/f
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalendarController that = (CalendarController) o;
        return model.getName().equals(that.getName());
    }
}
