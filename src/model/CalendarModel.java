package model;

import javafx.scene.paint.Color;
import view.DayView;

import java.util.*;

/**
 * CalendarModel represents a user profile with their events, this class stores all the
 * info needed for the view to display, there is no error handling, so inputs must be checked
 * by the controller before passing them into here.
 * @author Mahmood Gladney
 */
public class CalendarModel extends Observable{
    private String name;
    private Color color;
    private TreeMap<Date, List<CalendarEvent>> eventMap;
    private List<CalendarRecurringEvent> recurringEventList;

    /**
     * Constructs a new model object from a pre-existing set of events
     * @param name of the profile
     * @param eventMap
     * @param recurringEventList
     */
    public CalendarModel(String name, TreeMap<Date, List<CalendarEvent>> eventMap,
                         List<CalendarRecurringEvent> recurringEventList){
        this.name = name;
        this.eventMap = eventMap;
        this.recurringEventList = recurringEventList;
    }

    /**
     * Constructor for a new model object, with no events to start off with
     * @param name
     * @param color
     */
    public CalendarModel(String name, Color color){
        this.name = name;
        this.color = color;
        eventMap = new TreeMap<>();
        recurringEventList = new ArrayList<CalendarRecurringEvent>();
    }

    /**
     * Getter for the name of the profile
     * @return name of the profile
     */
    public String getName(){
        return name;
    }

    /**
     * Getter for the color
     * @return color
     */
    public Color getColor() { return color; }

    /**
     * Setter for the name of the profile
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Setter for the color of the profile
     * @param c Color object
     */
    public void setColor(Color c){ this.color = c; }
    public void addEvent(Date date, CalendarEvent event){
        if (eventMap.containsKey(date)) {
            eventMap.get(date).add(event);
        }
        else {
            List<CalendarEvent> tempList = new ArrayList<>();
            tempList.add(event);
            eventMap.put(date,tempList);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Returns all the events on a given day
     * @param date Date object representing the day you want events from
     * @return a list of all the events on the given date
     */
    public List<CalendarEvent> getEvents(Date date){
        List<CalendarEvent> ret = new ArrayList<>();
        for(CalendarRecurringEvent e : recurringEventList){

            System.out.println(e.getTitle() + " | " + e.getDate() + " | " + date + " " + DayView.addToDate(date, 1) + " | " + e.getInterval());

            if(!e.getOccurances(date, DayView.addToDate(date, 1)).isEmpty()){
                ret.add(e);
            }
        }

        if(eventMap.containsKey(date)){
            ret.addAll(eventMap.get(date));
        }

        if(!ret.isEmpty()){
            return ret;
        }else{
            return null;
        }
    }

    /**
     * Adds a recurring event to the calendar
     * @param event recurring event to be added
     */
    public void addRecurringEvent(CalendarRecurringEvent event){
        recurringEventList.add(event);
        setChanged();
        notifyObservers();
    }
    /**
     * Removes a recurring event from the calendar
     * @param event recurring event to be remove
     */
    public void removeRecurringEvent(CalendarRecurringEvent event){
        recurringEventList.remove(event);
        setChanged();
        notifyObservers();
    }

    /**
     * Returns a list of all recurring events in the calendar
     * @return ist of all recurring events
     */
    public List<CalendarRecurringEvent> getRecurringEventList(){
        return recurringEventList;
    }

    /**
     * Returns all of the events that this calendar holds
     *
     * This is used for saving the calendar to a file so that all of the events
     * can be saved and don'thave to be requested date by date. All of the lists of events are
     * put into a single list of events and returned
     *
     * @return the map of all events
     */
    public List<CalendarEvent> getEventList() {
        List<CalendarEvent> events = new ArrayList<CalendarEvent>();

        for (List<CalendarEvent> l : eventMap.values())
            events.addAll(l);

        return events;
    }

    /**
     * Matches a uuid with an event object and then removes it from the calendar
     * @param uuid id of the event to be removed
     * @throws IllegalArgumentException if the event was not found in the model
     */
    public void removeEvent(String uuid) throws IllegalArgumentException{
        for (List<CalendarEvent> i: eventMap.values()){
            for (CalendarEvent currEvent: i){
                if (currEvent.getEventId() == uuid){
                    i.remove(currEvent);
                }
            }
        }
        setChanged();
        notifyObservers();
    }
    /**
     * Removes the passed in event object from the Model
     * @param date date of the event
     * @param event event object to be removed
     * @throws IllegalArgumentException if the event was not found in the model
     */
    public void removeEvent(Date date, CalendarEvent event) throws IllegalArgumentException{
        boolean isRecurring = false;
        for(CalendarRecurringEvent e : recurringEventList){
            if (e.getEventId().equals(event.getEventId())) {
                isRecurring = true;
                break;
            }
        }

        if(isRecurring){
            removeRecurringEvent((CalendarRecurringEvent) event);
        }else{
            List<CalendarEvent> eventList = eventMap.get(date);
            boolean x = eventList.remove(event);
            if (x == false) {
                throw new IllegalArgumentException();}
        }

        setChanged();
        notifyObservers();
    }
}
