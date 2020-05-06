package model;

import javafx.scene.paint.Color;
import view.Day;
import view.DayView;

import javax.swing.plaf.synth.SynthUI;
import java.util.*;

public class CalendarModel extends Observable{
    private String name;
    private Color color;
    private TreeMap<Date, List<CalendarEvent>> eventMap;
    private List<CalendarRecurringEvent> recurringEventList;

    public CalendarModel(String name, TreeMap<Date, List<CalendarEvent>> eventMap,
                         List<CalendarRecurringEvent> recurringEventList){
        this.name = name;
        this.eventMap = eventMap;
        this.recurringEventList = recurringEventList;
    }

    public CalendarModel(String name, Color color){
        this.name = name;
        this.color = color;
        eventMap = new TreeMap<>();
        recurringEventList = new ArrayList<CalendarRecurringEvent>();
    }
    public String getName(){
        return name;
    }
    public Color getColor() { return color; }
    public void setName(String name){
        this.name = name;
    }
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
