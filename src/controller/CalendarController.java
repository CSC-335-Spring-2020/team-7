package controller;

import javafx.scene.paint.Color;
import model.CalendarEvent;
import model.CalendarModel;
import model.CalendarRecurringEvent;

import java.io.*;
import java.util.*;


/**
 * This is the controller for the Calender App.
 *
 * Purpose : To take data from the model, organize it, and send it to the view,
 * as well as taking data from the view, and adding it to the model.
 *
 * @author Amin Sennour
 * @author Joseph Acevedo
 */

public class CalendarController {

    public CalendarModel model;

    private static final String BEGIN_VEVENT = "BEGIN:VEVENT";
    private static final String END_VEVENT = "END:VEVENT";

    private static final String SUMMARY_ICS = "SUMMARY";
    private static final String DATE_START_ICS = "DTSTART";
    private static final String DATE_END_ICS = "DTEND";
    private static final String LOCATION_ICS = "LOCATION";
    private static final String DESCRIPTION_ICS = "DESCRIPTION";
    private static final String UUID_ICS = "UID";
    private static final String RECUR_ICS = "RRULE";
    private static final String COLOR_ICS = "COLOR";
    private static final String ICS_NL = "\r\n";

    private static final String BEGIN_VCAL = "BEGIN:VCALENDAR" + ICS_NL +
                                             "VERSION:2.0" + ICS_NL +
                                             "CALSCALE:GREGORIAN" + ICS_NL;
    private static final String STATUS_ICS = "STATUS:CONFIRMED";
    private static final String SEQUENCE_ICS = "SEQUENCE:0";
    private static final String END_VCAL = "END:VCALENDAR";

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

    /**
     * Takes the information needed to create a recurring event, creates it, and adds it
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
     * @param interval time between events repeating
     */
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
     * Reads an .ics file and creates a new CalendarModel from it
     *
     * The name of the file is used as the name of the calendar
     *
     * @param filename the name of the file to read in
     * @return A calendar generated from the given file
     */
    public static CalendarModel importCalendarFromFile(String filename) throws IOException {
        String calName = filename.substring(0, filename.indexOf('.'));

        // get rid of the path to the autosave location
        calName = calName.replace(CalendarAutoSave.PATH_TO_SAVES + "/", "");

        TreeMap<Date, List<CalendarEvent>> eventMap = new TreeMap<Date, List<CalendarEvent>>();
        List<CalendarRecurringEvent> recEvents = new ArrayList<CalendarRecurringEvent>();
        Color calColor = null;

        // Build the calendar using a pre-made ics parser
        Scanner in = new Scanner(new File(filename));
        while (in.hasNextLine()) {
            String currCmd = in.nextLine();

            if (currCmd.startsWith(BEGIN_VEVENT)) {
                CalendarEvent event = parseEvent(in);
                if (event instanceof CalendarRecurringEvent) {
                    recEvents.add( (CalendarRecurringEvent) event);
                } else {
                    if (eventMap.containsKey(event.getDate())) {
                        eventMap.get(event.getDate()).add(event);
                    } else {
                        List<CalendarEvent> events = new ArrayList<CalendarEvent>();
                        events.add(event);
                        eventMap.put(event.getDate(), events);
                    }
                }
            } else if (currCmd.startsWith(COLOR_ICS)) {
                int red = Integer.valueOf(currCmd.substring(8, 10), 16);
                int green = Integer.valueOf(currCmd.substring(10, 12), 16);
                int blue = Integer.valueOf(currCmd.substring(12, 14), 16);
                calColor = new Color(red / 255.0, green / 255.0, blue / 255.0, 1);
            }
        }
        CalendarModel calOut = new CalendarModel(calName, eventMap, recEvents);
        calOut.setColor(calColor);
        in.close();

        return calOut;
    }

    /**
     * Parses an event from the file
     *
     * Assumes the last line read from the input line was the beginning of a VEVENT
     * tag. Parses out the title, id, location, notes, date, start and end time and
     * returns an event based on it
     *
     * @param in The file to parse the event from
     * @return A calendar event based on the event from the ICS file
     */
    private static CalendarEvent parseEvent(Scanner in) {
        String title = "";
        String id = "";
        String location = "";
        String notes = "";
        Date date = null;
        Date start = null;
        Date end = null;
        Color calColor = null;
        int frequency = -1;

        while (in.hasNextLine()) {
            String currCmd = in.nextLine();
            currCmd = currCmd.replace(";", ":");
            String[] cmdParts = currCmd.split(":");
            String cmdType = cmdParts[0];
            String cmdArg = cmdParts.length > 1 ? cmdParts[1] : "";

            if (currCmd.startsWith(END_VEVENT)) {
                break;
            }
            switch(cmdType) {
                case SUMMARY_ICS:
                    title = cmdArg;
                    break;
                case DATE_START_ICS:
                    String dateStartStr = currCmd.substring(currCmd.length() - 15);
                    date = parseDate(dateStartStr);
                    start = parseDateTime(dateStartStr);
                    break;
                case DATE_END_ICS:
                    String dateEndStr = currCmd.substring(currCmd.length() - 15);;
                    end = parseDateTime(dateEndStr);
                    break;
                case LOCATION_ICS:
                    location = cmdArg;
                    break;
                case DESCRIPTION_ICS:
                    notes = cmdArg;
                    break;
                case UUID_ICS:
                    id = cmdArg;
                    break;
                case RECUR_ICS:
                    String freq = currCmd.split("(?=FREQ=)(\\w+)")[1].substring(1);
                    if (freq.equalsIgnoreCase("DAILY")) {
                        frequency = CalendarRecurringEvent.DAILY;
                    } else if (freq.equalsIgnoreCase("WEEKLY")) {
                        frequency = CalendarRecurringEvent.WEEKLY;
                    } else if (freq.equalsIgnoreCase("MONTHLY")) {
                        frequency = CalendarRecurringEvent.MONTHLY;
                    } else if (freq.equalsIgnoreCase("YEARLY")) {
                        frequency = CalendarRecurringEvent.YEARLY;
                    }
                    break;
            }
        }

        if (frequency != -1) {
            date.setHours(12);
            CalendarEvent event = new CalendarRecurringEvent(title, date, start, end, id, frequency);
            event.setLocation(location);
            event.setNotes(notes);
            return event;
        } else {
            CalendarEvent event = new CalendarEvent(title, date, start, end, id);
            event.setLocation(location);
            event.setNotes(notes);
            return event;
        }
    }

    /**
     * Parses a date from a given string in RFC1123 format, without the time
     *
     * The dates are stored in the file in a format similar to RFC1123, except without
     * the colons and dashes. The date should be in the format yyyymmmddThhmmss.
     *
     * @param args The string storing the date to parse
     * @return A date form the given string
     */
    private static Date parseDate(String args) {
        int year = Integer.parseInt(args.substring(0, 4)) - 1900;
        int month = Integer.parseInt(args.substring(4, 6));
        int day = Integer.parseInt(args.substring(6, 8));
        return new Date(year, month, day);
    }

    /**
     * Parses a date from a given string in RFC1123 format, with the time
     *
     * The dates are stored in the file in a format similar to RFC1123, except without
     * the colons and dashes. The date should be in the format yyyymmmddThhmmss.
     *
     * @param args The string storing the date to parse
     * @return A date form the given string
     */
    private static Date parseDateTime(String args) {
        int year = Integer.parseInt(args.substring(0, 4)) - 1900;
        int month = Integer.parseInt(args.substring(4, 6));
        int day = Integer.parseInt(args.substring(6, 8));
        int hour = Integer.parseInt(args.substring(9, 11));
        int minute = Integer.parseInt(args.substring(11, 13));
        int sec = Integer.parseInt(args.substring(13, 15));
        return new Date(year, month, day, hour, minute, sec);
    }

    public static void exportCalendarToFile(CalendarModel calendarModel) throws IOException {
        exportCalendarToFile(calendarModel, CalendarAutoSave.PATH_TO_SAVES + "/" + calendarModel.getName() + ".ics");
    }

    public static void exportCalendarToFile(CalendarModel calendar, String pathname) throws IOException {
        FileWriter out = new FileWriter(new File(pathname));
        List<CalendarEvent> events = calendar.getEventList();
        List<CalendarRecurringEvent> recEvents = calendar.getRecurringEventList();

        out.write(BEGIN_VCAL);
        out.write(String.format("%s:%s%s", COLOR_ICS, calendar.getColor(), ICS_NL));

        for (CalendarEvent event : events) {
            writeEventToFile(event, out);
        }
        for (CalendarRecurringEvent event : recEvents) {
            writeRecEventToFile(event, out);
        }

        out.write(END_VCAL);
        out.close();
    }

    private static void writeEventToFile(CalendarEvent event, FileWriter out) throws IOException {
        String toOutput = BEGIN_VEVENT + ICS_NL +
                          SUMMARY_ICS + ':' + event.getTitle() + ICS_NL +
                          DATE_START_ICS + ';' + convertDateToString(event.getStartTime()) + ICS_NL +
                          DATE_END_ICS + ';' + convertDateToString(event.getEndTime()) + ICS_NL +
                          LOCATION_ICS + ':' + event.getLocation() + ICS_NL +
                          DESCRIPTION_ICS + ':' + event.getNotes() + ICS_NL +
                          STATUS_ICS + ICS_NL +
                          UUID_ICS + ':' + event.getEventId() + ICS_NL +
                          SEQUENCE_ICS + ICS_NL +
                          END_VEVENT + ICS_NL;
        out.write(toOutput);
    }

    private static void writeRecEventToFile(CalendarRecurringEvent event, FileWriter out) throws IOException {
        String rec = event.getInterval();

        //System.out.println("Writing date to file: " + event.getStartTime());
        String toOutput = BEGIN_VEVENT + ICS_NL +
                SUMMARY_ICS + ':' + event.getTitle() + ICS_NL +
                DATE_START_ICS + ';' + convertDateToString(event.getStartTime()) + ICS_NL +
                DATE_END_ICS + ';' + convertDateToString(event.getEndTime()) + ICS_NL +
                LOCATION_ICS + ':' + event.getLocation() + ICS_NL +
                DESCRIPTION_ICS + ':' + event.getNotes() + ICS_NL +
                RECUR_ICS + ':' + "FREQ=" + rec + ICS_NL +
                STATUS_ICS + ICS_NL +
                UUID_ICS + ':' + event.getEventId() + ICS_NL +
                SEQUENCE_ICS + ICS_NL +
                END_VEVENT + ICS_NL;
        out.write(toOutput);
    }

    /**
     * Converts a Date into a string representing the date in a form similar to
     * RFC1123, but with none of the punctuation. The date is formatted as follows:
     * yyyymmddThhmmss
     *
     * @param d The date to convert into a string
     * @return The string representing the given date
     */
    private static String convertDateToString(Date d) {
        String out = String.format("%04d%02d%02dT%02d%02d%02d",
                d.getYear() + 1900, d.getMonth(), d.getDate(), d.getHours(), d.getMinutes(), d.getSeconds());
        return out;
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
