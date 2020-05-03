package model;

import javax.swing.plaf.synth.SynthUI;
import java.util.*;

public class CalendarModel extends Observable{
    private String name;
    private TreeMap<Date, List<CalendarEvent>> eventMap;
    private List<CalendarRecurringEvent> recurringEventList;

    public CalendarModel(String name, TreeMap<Date, List<CalendarEvent>> eventMap,
                         List<CalendarRecurringEvent> recurringEventList){
        this.name = name;
        this.eventMap = eventMap;
        this.recurringEventList = recurringEventList;
    }

    public CalendarModel(String name){
        this.name = name;
        eventMap = new TreeMap<>();
        recurringEventList = new ArrayList<CalendarRecurringEvent>();
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
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
    public void removeEvent(Date date, CalendarEvent event) throws IllegalArgumentException{
        List<CalendarEvent> eventList = eventMap.get(date);
        boolean x = eventList.remove(event);
        if (x == false) {
            throw new IllegalArgumentException();}
        setChanged();
        notifyObservers();
    }

    public List<CalendarEvent> getEvents(Date date){
        if(eventMap.containsKey(date)){
            return eventMap.get(date);
        }else{
            return null;
        }
    }

    public void addRecurringEvent(CalendarRecurringEvent event){
        recurringEventList.add(event);
        setChanged();
        notifyObservers();
    }
    public void removeRecurringEvent(CalendarRecurringEvent event){
        recurringEventList.remove(event);
        setChanged();
        notifyObservers();
    }
    public List<CalendarRecurringEvent> getRecurringEventList(){
        return recurringEventList;
    }

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
}
