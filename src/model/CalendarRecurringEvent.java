package model;

import java.util.*;

/**
 * This subclass of CalendarEvent is used for events that recur at a
 * regular interval
 * @author Joseph Acevedo
 */
public class CalendarRecurringEvent extends CalendarEvent {

    public static final int DAILY = Calendar.DATE;
    public static final int WEEKLY = Calendar.WEEK_OF_MONTH;
    public static final int MONTHLY = Calendar.MONTH;
    public static final int YEARLY = Calendar.YEAR;

    public static final Map<String, Integer> intervals = buildMap();

    /**
     * Constructs a map for reference and returns it
     * @return ret
     */
    public static Map<String, Integer> buildMap(){
        Map<String, Integer> ret = new TreeMap<>();
        ret.put("DAILY", DAILY);
        ret.put("WEEKLY", WEEKLY);
        ret.put("MONTHLY", MONTHLY);
        ret.put("YEARLY", YEARLY);
        return ret;
     }

    private int interval;

    /**
     * Constructs a new recurring event object
     * @param title title of the event
     * @param date Date object with the current date, zero out the time
     * @param start Date object with the start time/day
     * @param end Date object with the end time/day
     * @param id unique id string
     * @param interval number of days between repeat events
     */
    public CalendarRecurringEvent(String title, Date date, Date start, Date end, String id, int interval) {
        super(title, date, start, end, id);
        this.interval = interval;
    }

    /**
     * Returns the interval of this recurring event
     * @return the interval
     */
    public String getInterval() {
        for(String k : intervals.keySet()){
            if(intervals.get(k) == this.interval){
                return k;
            }
        }
        return "NONE";
    }

    /**
     * Sets the interval of this recurring event
     * @param interval the interval
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * Returns all of the occurances of this event between the given dates
     *
     * @param start The start date of the window
     * @param end The end date of the window
     * @return A list of Dates that represent the occurrances of this event in the window
     */
    public List<Date> getOccurances(Date start, Date end) {
        List<Date> occurrences = new ArrayList<Date>();
        Calendar cal = Calendar.getInstance();

        cal.setTime(this.getDate());
        while (cal.getTime().before(end)) {
            if (cal.getTime().after(start))
                occurrences.add(cal.getTime());
            cal.add(interval, 1);
        }
        return occurrences;
    }
    
}


