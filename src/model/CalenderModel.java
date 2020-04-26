package model;

import java.util.*;

public class CalenderModel {
    private String name;
    private TreeMap<Date, List<CalenderEvent>> eventMap;
    private List<CalenderReoccuringEvent> reoccuringEventList;

    public CalenderModel(String name, TreeMap<Date, List<CalenderEvent>> eventMap,
                         List<CalenderReoccuringEvent> reoccuringEventList){
        this.name = name;
        this.eventMap = eventMap;
        this.reoccuringEventList = reoccuringEventList;
    }

    public CalenderModel(String name){
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
    public void addEvent(Date date, CalenderEvent event){
        if (eventMap.containsKey(date)) {
            eventMap.get(date).add(event);
        }
        else {
            List<CalenderEvent> tempList = new ArrayList<>();
            tempList.add(event);
            eventMap.put(date,tempList);
        }
    }
    public void removeEvent(Date date, CalenderEvent event) throws IllegalArgumentException{
        List<CalenderEvent> eventList = eventMap.get(date);
        boolean x = eventList.remove(event);
        if (x == false) {
            throw new IllegalArgumentException();}
    }

    public List<CalenderEvent> getEvents(Date date){
        return eventMap.get(date);
    }

    public void addReoccuringEvent(CalenderReoccuringEvent event){
        reoccuringEventList.add(event);
    }
    public void removeReoccuringEvent(CalenderReoccuringEvent event){
        reoccuringEventList.remove(event);
    }
    public List<CalenderReoccuringEvent> getReoccuringEventList(){
        return reoccuringEventList;
    }
}
