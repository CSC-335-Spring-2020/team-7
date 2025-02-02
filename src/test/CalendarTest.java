package test;

import controller.CalendarAutoSave;
import controller.CalendarController;
import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.CalendarModel;
import model.CalendarRecurringEvent;
import org.junit.jupiter.api.Test;
import view.CalendarView;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static controller.CalendarController.importCalendarFromFile;
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

    @Test
    public void testModel(){
        TreeMap eventMap = new TreeMap<Date, List<CalendarEvent>>();
        List recurringEvents = new ArrayList<CalendarRecurringEvent>();
        Date start = new Date();
        Date end = new Date();
        CalendarEvent event = new CalendarEvent("TestEvent", start, start, end, "1");
        CalendarRecurringEvent rEvent = new CalendarRecurringEvent("TestEvent", start, start, end, "2", 10);
        CalendarModel m = new CalendarModel("Test", eventMap, recurringEvents);
        m.getName();
        m.getColor();
        m.setName("Change");
        m.setColor(Color.BLUE);
        m.addEvent(start, event);
        m.addEvent(start, event);
        m.addRecurringEvent(rEvent);
        m.getEvents(start);
        m.removeEvent(start, rEvent);
        m.getEvents(new Date(2000, 0, 1));
        m.getEventList();
        m.getRecurringEventList();
        m.addRecurringEvent(rEvent);
        m.removeRecurringEvent(rEvent);
        m.removeEvent("1");
        m.removeEvent(start, event);
        m.getEvents(start);
        m = new CalendarModel("Test", Color.BLACK);
    }

    @Test
    public void testControllerButNotFileImports(){
        TreeMap eventMap = new TreeMap<Date, List<CalendarEvent>>();
        List recurringEvents = new ArrayList<CalendarRecurringEvent>();
        CalendarModel m = new CalendarModel("Test", eventMap, recurringEvents);
        CalendarController c = new CalendarController(m);
        CalendarController d = new CalendarController(m);
        Date start = new Date(2000, 3, 1);
        Date middle = new Date(2000, 4, 1);
        Date end = new Date(2000, 4, 10);
        CalendarRecurringEvent rEvent = new CalendarRecurringEvent("TestEvent", start, start, end, "2", 10);
        c.getName();
        c.setName("Changed");
        c.getColor();
        c.setColor(Color.BLUE);
        c.getEventsOnDay(start);
        c.addEvent("Start", start, start, end, "Somewhere", "No");
        c.getEventsOnDay(start);
        c.addRecurringEvent("None", start, start, end, "","", 4);
        c.toString();
        boolean s = c.equals(d);
        assertEquals(s, true);
    }

    @Test
    public void testCalendarAutoSave(){
        // this bit just runs the threading code
        CalendarAutoSave.launchAutoSave(CalendarAutoSave.getSavedCalendars(null));
        CalendarAutoSave.closeAutoSave();

        int numberOfCalendars = 0;
        for (final File fileEntry : Objects.requireNonNull(new File(CalendarAutoSave.PATH_TO_SAVES).listFiles())) {
            numberOfCalendars += 1;
        }
        assertEquals(numberOfCalendars, CalendarAutoSave.getSavedCalendars(null).size());
        CalendarAutoSave.saveCalendars(CalendarAutoSave.getSavedCalendars(null));
        assertEquals(numberOfCalendars, CalendarAutoSave.getSavedCalendars(null).size());
    }

    @Test
    public void testICSSaveandLoad() {
        CalendarEvent event = new CalendarEvent("Non-Recurring Event",
                new Date(2020 - 1900, 4, 25),
                new Date(2020 - 1900, 4, 25, 8, 30, 0),
                new Date(2020 - 1900, 4, 25, 9, 30, 0),
                "JSGFS6TF8S76FTA");

        System.out.println(event.getStartTime());

        CalendarRecurringEvent recEvent = new CalendarRecurringEvent("Recurring Event",
                new Date(2020 - 1900, 4, 25),
                new Date(2020 - 1900, 4, 25, 8, 30, 0),
                new Date(2020 - 1900, 4, 25, 9, 30, 0),
                "KJADYFT87SDTFUG",
                CalendarRecurringEvent.WEEKLY);

        CalendarModel calendar = new CalendarModel("SampleCalendar", Color.RED);
        calendar.addEvent(event.getDate(), event);
        calendar.addRecurringEvent(recEvent);

        try {

            CalendarController.exportCalendarToFile(calendar);
            CalendarModel readCal = CalendarController.importCalendarFromFile("saves/SampleCalendar.ics");
            CalendarEvent newEvent = readCal.getEventList().get(0);
            CalendarRecurringEvent newRecEvent = readCal.getRecurringEventList().get(0);

            assertEquals(newEvent.getTitle(), event.getTitle());
            assertEquals(newEvent.getDate(), event.getDate());
            assertEquals(newEvent.getStartTime(), event.getStartTime());
            assertEquals(newEvent.getEndTime(), event.getEndTime());
            assertEquals(newEvent.getLocation(), event.getLocation());
            assertEquals(newEvent.getNotes(), event.getNotes());
            assertEquals(newEvent.getEventId(), event.getEventId());
        } catch (IOException e) {
            assert(false);
        }
    }
}
