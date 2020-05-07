package controller;

import model.CalendarModel;
import view.CalendarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Static class to wrap the logic of autosaving and loading all the user's
 * saved calendars
 *
 * This class can't get 90 percent coverage due to it interfacing with files
 * and threads.
 *
 * @author Amin Sennour
 * @author Joseph Acevedo
 */

public class CalendarAutoSave {
    public static final String PATH_TO_SAVES = "saves";

    private static Thread autoSave;
    /**
     * Starts a thread to autosave
     * @param c List of calendar controllers
     */
    public static void launchAutoSave(List<CalendarController> c){
        autoSave = new Thread(()->{
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

    /**
     * Stops the autosave
     */
    public static void closeAutoSave(){
        autoSave.stop();
    }
    /**
     * Returns a list of saved calendars
     * @param observer
     * @return List of calendar controllers
     */
    public static List<CalendarController> getSavedCalendars(CalendarView observer){
        List<CalendarController> c = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(new File(PATH_TO_SAVES).listFiles())) {
            try {
                CalendarModel m = CalendarController.importCalendarFromFile("saves/" + fileEntry.getName());
                if(observer!= null){
                    m.addObserver(observer);
                }
                c.add(new CalendarController(m));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return c;
    }
    /**
     * Saves all the calendars inside the passed in list
     * @param c List of calendar controller
     */
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
