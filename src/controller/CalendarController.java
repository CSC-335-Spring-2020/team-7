package controller;

import model.CalendarEvent;
import model.CalendarModel;
import model.CalendarRecurringEvent;

import java.io.*;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the controller for the Calender App.
 *
 * Purpose : To take data from the model, organize it, and send it to the view,
 * as well as taking data from the view, and adding it to the model.
 *
 */

public class CalendarController {

    CalendarModel model;

    private static final String BEGIN_VEVENT = "BEGIN:VEVENT";
    private static final String END_VEVENT = "END:VEVENT";

    private static final String SUMMARY_ICS = "SUMMARY";
    private static final String DATE_START_ICS = "DTSTART";
    private static final String DATE_END_ICS = "DTEND";
    private static final String LOCATION_ICS = "LOCATION";
    private static final String DESCRIPTION_ICS = "DESCRIPTION";
    private static final String UUID_ICS = "UID";
    private static final String RECUR_ICS = "RRULE";
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
     * Reads an .ics file and creates a new CalendarModel from it
     *
     * The name of the file is used as the name of the calendar
     *
     * @param filename the name of the file to read in
     * @return A calendar generated from the given file
     */
    public static CalendarModel importCalendarFromFile(String filename) throws IOException {
        String calName = filename.substring(0, filename.indexOf('.'));
        TreeMap<Date, List<CalendarEvent>> eventMap = new TreeMap<Date, List<CalendarEvent>>();
        List<CalendarRecurringEvent> recEvents = new ArrayList<CalendarRecurringEvent>();

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
            }
        }
        CalendarModel calOut = new CalendarModel(calName, eventMap, recEvents);
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
        int year = Integer.parseInt(args.substring(0, 4));
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
        int year = Integer.parseInt(args.substring(0, 4));
        int month = Integer.parseInt(args.substring(4, 6));
        int day = Integer.parseInt(args.substring(6, 8));
        int hour = Integer.parseInt(args.substring(9, 11));
        int minute = Integer.parseInt(args.substring(11, 13));
        int sec = Integer.parseInt(args.substring(13, 15));
        return new Date(year, month, day, hour, minute, sec);
    }

    public static void exportCalendarToFile(CalendarModel calendar) throws IOException {
        FileWriter out = new FileWriter(new File(calendar.getName() + ".ics"));
        List<CalendarEvent> events = calendar.getEventList();
        List<CalendarRecurringEvent> recEvents = calendar.getRecurringEventList();

        out.write(BEGIN_VCAL);

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
        String rec = "DAILY";
        if (event.getInterval() == CalendarRecurringEvent.WEEKLY) {
            rec = "WEEKLY";
        } else if (event.getInterval() == CalendarRecurringEvent.MONTHLY) {
            rec = "MONTHLY";
        } else if (event.getInterval() == CalendarRecurringEvent.YEARLY) {
            rec = "YEARLY";
        }

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
                d.getYear(), d.getMonth(), d.getDay(), d.getHours(), d.getMinutes(), d.getSeconds());
        return out;
    }
}
