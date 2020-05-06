package view;

import controller.CalendarController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class allows for the construction of the calender app in a week view
 * setting.
 *
 * @author Amin Sennour
 * @author Mahmood Gladney
 */

public class WeekView {



    /**
     * The setCenter method which replaces the center with the
     * day view
     */
    protected static void setCenter(CalendarView calendarView, List<CalendarController> calenders,
                                    AtomicReference<Date> date, Date[] datesOfWeek, VBox centerPane){
        /*
         * Code for the forward / back buttons
         */
        Button left = new Button("<");
        left.setMaxHeight(20);
        left.setMaxWidth(Double.MAX_VALUE);
        left.setOnMouseClicked((e)->{
            date.set(addToDate(date.get(), -7));
            WeekView.populateDatesOfWeek(date,datesOfWeek);
            calendarView.update(null, null);
        });
        Button right = new Button(">");
        right.setOnMouseClicked((e)->{
            date.set(addToDate(date.get(), 7));
            populateDatesOfWeek(date,datesOfWeek);
            calendarView.update(null, null);
        });
        right.setMaxWidth(Double.MAX_VALUE);
        right.setMaxHeight(20);
        HBox buttons = new HBox(left, right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        buttons.setAlignment(Pos.CENTER);

        /*
         * Code for the scrolling view for the day
         */
        HBox hold = new HBox();

        VBox buttonsAndHold = new VBox(buttons, hold);
        buttonsAndHold.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buttonsAndHold.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));


        ScrollPane scrollPane = new ScrollPane(buttonsAndHold);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setPrefSize(300, 700);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        VBox.setMargin(scrollPane, new Insets(0,0,0,10));

        centerPane.getChildren().add(scrollPane);

        hold.prefHeightProperty().bind(scrollPane.heightProperty().add(-34));
        hold.prefWidthProperty().bind(scrollPane.widthProperty().add(-17));

        populateDatesOfWeek(date,datesOfWeek);

        boolean isFirst = true;
        for(Date d : datesOfWeek){
            Day.day(hold, calenders, d, isFirst);
            isFirst = false;
        }
    }

    /**
     * Method populates the dates of the week array based on a starting date
     *
     * @param date starting date
     * @param datesOfWeek dates of the week array
     */
    protected static void populateDatesOfWeek(AtomicReference<Date> date,Date[] datesOfWeek) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date.get());
        int starterDay = cal.get(Calendar.DAY_OF_WEEK);
        datesOfWeek[starterDay - 1] = date.get();
        Date tempDate = date.get();
        for (int i = starterDay;i<7;i++){
            tempDate = addToDate(tempDate, 1);
            datesOfWeek[i] = tempDate;
        }
        tempDate = date.get();
        for (int i = starterDay-2;i>=0;i--){
            tempDate = addToDate(tempDate, -1);
            datesOfWeek[i] = tempDate;
        }
    }

    /**
     * Helper Method which zero's house the time portion of the date
     *
     * @param day the day who's time to zero
     * @return the date with the zeroed time
     */
    protected static Date zeroOutTime(Date day){
        Date ret = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(day);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        strDate += " 00:00:00";

        try{
            ret = formatter.parse(strDate);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Simple method to encapsulate editing a date
     * @param date the date being edited
     * @param amount the amount to shift by
     * @return the new date
     */
    protected static Date addToDate(Date date, int amount){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, amount);
        return cal.getTime();
    }
}