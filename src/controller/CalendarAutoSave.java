package controller;

import model.CalendarModel;
import view.CalendarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Static class to wrap the logic of autosaving and loading all the user's
 * saved calendars
 */

public class CalendarAutoSave {
    public static final String PATH_TO_SAVES = "saves";

    public static void launchAutoSave(List<CalendarController> c){
        Thread autoSave = new Thread(()->{
            while (true){
                saveCalendars(c);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        autoSave.start();
    }

    public static List<CalendarController> getSavedCalendars(CalendarView observer){
        List<CalendarController> c = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(new File(PATH_TO_SAVES).listFiles())) {
            try {
                CalendarModel m = CalendarController.importCalendarFromFile("saves/" + fileEntry.getName());
                m.addObserver(observer);
                c.add(new CalendarController(m));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return c;
    }

    public static void saveCalendars(List<CalendarController> c){
        for(CalendarController controller : c){
            try {
                CalendarController.exportCalendarToFile(controller.model);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
