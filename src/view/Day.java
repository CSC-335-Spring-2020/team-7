package view;

import controller.CalendarController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.CalendarEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * This class is a static class which exists to provide the "day" method
 * which takes a parent, among other things, and draws a "day" within that
 * parent scene
 *
 * @author Amin Sennour
 */

public class Day {
    /**
     * The main method of this class, takes a parent and other information,
     * and draws the day within the parent
     *
     * @param parent the scene to draw in
     * @param calendars a list of CalenderControllers, all the calenders to
     *                  draw from
     * @param day the day being represented
     * @param showTime if the time bar should be shown or not
     */
    public static void day(Pane parent, List<CalendarController> calendars, Date day, boolean showTime){
        GridPane mainGrid = new GridPane();
        VBox.setVgrow(mainGrid, Priority.ALWAYS);
        mainGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        mainGrid.setGridLinesVisible(true);
        mainGrid.getStyleClass().add("grid-pane");
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
        SimpleDateFormat dowFormat = new SimpleDateFormat("EEEE");
        String strDate = dowFormat.format(day) + "\n" + dateFormat.format(day);

        Label l = new Label(strDate);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setPadding(new Insets(15,15,15,15));
        l.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        VBox.setVgrow(l, Priority.ALWAYS);

        VBox v = new VBox(l, mainGrid);
        v.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        v.prefHeightProperty().bind(parent.heightProperty());
        v.prefWidthProperty().bind(parent.widthProperty());

        addTime(showTime, mainGrid);
        for(CalendarController c : calendars){
            populateEvents(c, day, mainGrid);
        }


        parent.getChildren().add(v);
    }

    /**
     * Draws the time bar in the first column of mainGrid, or, if time
     * is not to be shown, this method sets up the grid with 23 rows
     *
     * @param showTime if time is to be shown or not
     * @param mainGrid the grid
     */
    private static void addTime(boolean showTime, GridPane mainGrid){
        ColumnConstraints cols = new ColumnConstraints();
        cols.setMaxWidth(30);
        cols.setMinWidth(30);
        cols.setHgrow(Priority.ALWAYS);
        mainGrid.getColumnConstraints().add(cols);

        for(int i = 0; i<24; i++){
            Label time = showTime ? new Label(Integer.toString(i)) : new Label("");
            time.minWidth(1);

            time.setPadding(new Insets(5, showTime ? 5 : 0, 5, showTime ? 5 : 0));
            time.setAlignment(Pos.CENTER_RIGHT);

            RowConstraints rows = new RowConstraints();
            rows.setMaxHeight(Double.MAX_VALUE);
            rows.setVgrow(Priority.ALWAYS);
            mainGrid.getRowConstraints().add(rows);

            mainGrid.add(time, 0, i);
        }
    }

    /**
     * This method serves to populate the grid with the events on day in c
     *
     * @param c the controller
     * @param day the day to look for
     * @param mainGrid the grid to draw on
     */
    private static void populateEvents(CalendarController c, Date day, GridPane mainGrid){
        List<CalendarEvent> events = c.getEventsOnDay(day);

        if(events == null) return;
        for(CalendarEvent e : events){
            int time = e.getStartTime().getHours();
            int endTime = e.getEndTime().getHours();

            for(int i = time; i <=endTime; i++){
                Label title = new Label(e.getTitle());
                title.setPadding(new Insets(5,5,5,5));
                title.setAlignment(Pos.CENTER_RIGHT);
                title.setOnMouseClicked((event)->{
                    AddEventModal m = new AddEventModal(false, e, c);
                    m.show();
                });
                mainGrid.addRow(i, title);
            }

            ColumnConstraints cols = new ColumnConstraints();
            cols.setMaxWidth(Double.MAX_VALUE);
            cols.setHgrow(Priority.ALWAYS);
            mainGrid.getColumnConstraints().add(cols);
        }
    }
}
