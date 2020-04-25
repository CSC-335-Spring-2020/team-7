package controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;


/**
 * This is the controller for the Calender App.
 *
 * Purpose : To take data from the model, organize it, and send it to the view,
 * as well as taking data from the view, and adding it to the model.
 *
 */

public class CalenderController {

    public List<CalendarEvent> getEvents(Date date){
        return model.getEvents(date);
    }

    public void addEvent(String title, Date date, int startTime, int endTime, String location, String notes){
        int uuid = new UUID(title).getLeastSignificantBits();
        CalendarEvent event = new CalenderEvent(title, date, startTime, endTime, location, notes);
        model.addEvent(event);

    }

    public void removeEvent(CalendarEvent toRemove){
        model.removeEvent(toRemove);
    }
}
