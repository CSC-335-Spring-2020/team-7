package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class CalendarRecurringEvent extends CalendarEvent {

    public static final int DAILY = Calendar.DATE;
    public static final int WEEKLY = Calendar.WEEK_OF_MONTH;
    public static final int MONTHLY = Calendar.MONTH;
    public static final int YEARLY = Calendar.YEAR;

    private int interval;

    public CalendarRecurringEvent(String title, Date date, Date start, Date end, String id, int interval) {
        super(title, date, start, end, id);
        this.interval = interval;
    }

    /**
     * Returns the interval of this recurring event
     * @return the interval
     */
    public int getInterval() {
        return this.interval;
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
        while (cal.getTime().after(start)) {
            if (cal.getTime().before(end))
                occurrences.add(cal.getTime());
            cal.add(interval, -1);
        }

        cal.setTime(this.getDate());
        while (cal.getTime().before(end)) {
            if (cal.getTime().after(start))
                occurrences.add(cal.getTime());
            cal.add(interval, 1);
        }
        return occurrences;
    }
}
