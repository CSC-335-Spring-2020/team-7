package view;

import controller.CalendarController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DayView extends  CalendarView {

    /**
     * The overriden setCenter method which replaces the center with the
     * day view
     */
    @Override
    protected void setCenter(){
        resetCenter();
        List<CalendarController> calenders = new ArrayList<>();
        calenders.add(c);

        // calling the constructor will cause the day to be drawn to the screen
        Date day = new Date();
        day.setTime(0);
        System.out.println(day);
        Day view = new Day(centerPane, calenders, day, true);
    }
}
