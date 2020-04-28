package model;

import java.util.*;

public class CalendarModel {
    private String name;
    private TreeMap<Date, List<CalendarEvent>> eventMap;
    private List<CalendarReoccuringEvent> reoccuringEventList;

    public CalendarModel(String name, TreeMap<Date, List<CalendarEvent>> eventMap,
                         List<CalendarReoccuringEvent> reoccuringEventList){
        this.name = name;
        this.eventMap = eventMap;
        this.reoccuringEventList = reoccuringEventList;
    }

    public CalendarModel(String name){
        this.name = name;
        eventMap = new TreeMap<>();
        reoccuringEventList = new ArrayList<>();
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
    }
    public void removeEvent(Date date, CalendarEvent event) throws IllegalArgumentException{
        List<CalendarEvent> eventList = eventMap.get(date);
        boolean x = eventList.remove(event);
        if (x == false) {
            throw new IllegalArgumentException();}
    }

    public List<CalendarEvent> getEvents(Date date){
        return eventMap.get(date);
    }

    public void addReoccuringEvent(CalendarReoccuringEvent event){
        reoccuringEventList.add(event);
    }
    public void removeReoccuringEvent(CalendarReoccuringEvent event){
        reoccuringEventList.remove(event);
    }
    public List<CalendarReoccuringEvent> getReoccuringEventList(){
        return reoccuringEventList;
    }
}
