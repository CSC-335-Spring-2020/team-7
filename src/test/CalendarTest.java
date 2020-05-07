package test;

import model.CalendarEvent;
import model.CalendarRecurringEvent;
import org.junit.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalendarTest {
    @Test
    public void testEvent(){
        Date currDate = new Date();
        Date startDate = new Date(2020,3,12);
        Date endDate = new Date(2020,3,14);
        CalendarEvent event = new CalendarEvent(null,null,null,null,"testID");
        event.setDate(currDate);
        event.setStartTime(startDate);
        event.setEndTime(endDate);
        event.setNotes("Pick up the flowers first");
        event.setLocation("3321 W 45th St");
        event.setTitle("Date night");
        assertEquals(event.getDate(), currDate);
        assertEquals(event.getTitle(),"Date night");
        assertEquals(event.getEndTime(),endDate);
        assertEquals(event.getEventId(),"testID");
        assertEquals(event.getNotes(), "Pick up the flowers first");
        assertEquals(event.getStartTime(), startDate);
        assertEquals(event.getLocation(),"3321 W 45th St");
    }
    @Test
    public void testRecurringEvent(){
        Date currDate = new Date();
        Date startDate = new Date(2020,3,12);
        Date endDate = new Date(2020,3,29);
        CalendarRecurringEvent event = new CalendarRecurringEvent(null,null,null,null,"testID",0);
        event.setDate(currDate);
        event.setStartTime(startDate);
        event.setEndTime(endDate);
        event.setNotes("Pick up the flowers first");
        event.setLocation("3321 W 45th St");
        event.setTitle("Date night");
        event.setInterval(4);
        assertEquals(event.getDate(), currDate);
        assertEquals(event.getTitle(),"Date night");
        assertEquals(event.getEndTime(),endDate);
        assertEquals(event.getEventId(),"testID");
        assertEquals(event.getNotes(), "Pick up the flowers first");
        assertEquals(event.getStartTime(), startDate);
        assertEquals(event.getLocation(),"3321 W 45th St");
        assertEquals(event.getInterval(), "WEEKLY");
        assertEquals(event.getOccurances(startDate,endDate).size(),3);
        assertEquals(event.getOccurances(endDate,startDate).size(),0);
    }


}
