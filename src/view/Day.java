package view;

import controller.CalendarController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import model.CalendarEvent;

import java.util.Date;
import java.util.List;

/**
 * This class serves as the center component for both the week and day view
 * classes. It represents a time referenced verticle table of times and events
 * the pane that it should fill should be passed to the constructor as a
 * parameter
 */

public class Day {
    boolean showTime;
    Date day;
    VBox time;

    public Day(Pane parent, List<CalendarController> calendars, Date day, boolean showTime){
        this.showTime = showTime;
        this.day = day;



        VBox time = new VBox();
        VBox.setVgrow(time, Priority.ALWAYS);
        this.time = time;
        time.setPadding(new Insets(10,10,10,10));
        time.setMaxWidth(55);
        time.setBackground(new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));


        VBox events = new VBox();

        ScrollPane scrollPane = new ScrollPane(time);
        parent.getChildren().add(scrollPane);

        generateFrame();
        for(CalendarController c : calendars){
            populateFrame(c);
        }
    }

    /**
     * This generates a frame for the grid, specifically the vertical box of
     * Hboxes that will contain the events. As well as the vbox of time, if the
     * showTime boolean is true
     */
    public void generateFrame(){
        for(int i = 0; i < 24; i++){
            Label newTime = new Label(Integer.toString(i));
            newTime.setPrefHeight(10);
            VBox.setVgrow(newTime, Priority.ALWAYS);
            time.getChildren().add(newTime);
        }
    }

    public void populateFrame(CalendarController c){
        List<CalendarEvent> events = c.getEventsOnDay(day);
    }


}
