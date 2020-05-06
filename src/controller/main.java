package controller;

import javafx.scene.paint.Color;
import model.CalendarModel;
import model.CalendarRecurringEvent;

import java.util.Date;

public class main {
    public static void main(String[] args) {
        CalendarController c = new CalendarController(new CalendarModel("TEST", Color.OLDLACE));

        Date startTime = new Date();
        startTime.setHours(0);
        startTime.setMinutes(0);
        startTime.setSeconds(0);

        Date endTime = new Date();
        endTime.setHours(23);
        endTime.setMinutes(0);
        endTime.setSeconds(0);

        c.addRecurringEvent("Title", new Date(), startTime, endTime, "", "", CalendarRecurringEvent.DAILY);
        c.addEvent("Title", new Date(), startTime, endTime, "", "");

        Date toGet = new Date();
        toGet.setHours(0);
        toGet.setMinutes(0);
        toGet.setSeconds(0);

        System.out.println(c.getEventsOnDay(toGet));

    }
}
