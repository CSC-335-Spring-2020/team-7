package view;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import controller.CalendarController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import model.CalendarEvent;

/**
 * @author Nicholas Lindenberg
 *
 * Creates a JavaFx window that populates a Calendar based on a Month View.
 */
public class MonthView {
    private static String[] months = {"January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"};


    /**
     * Populates the borderpane center with the correct date information based on the parameters.
     *
     * @param calendarView - View object this will affect.
     * @param date - Date containing the relevant information.
     * @param centerPane - Pane from View object affected by these changes.
     */
    protected static void setCenter(CalendarView calendarView, List<CalendarController> c, AtomicReference<Date> date, VBox centerPane) {
        // BUTTONS
        Button left = new Button("<");
        left.setMaxHeight(20);
        left.setMaxWidth(Double.MAX_VALUE);
        left.setOnMouseClicked((e)->{
            date.set(addToDate(date.get(), -1));
            calendarView.update(null, null);
        });
        Button right = new Button(">");
        right.setOnMouseClicked((e)->{
            date.set(addToDate(date.get(), 1));
            calendarView.update(null, null);
        });
        right.setMaxWidth(Double.MAX_VALUE);
        right.setMaxHeight(20);
        HBox buttons = new HBox();
        buttons.getChildren().add(left);
        buttons.getChildren().add(right);
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        buttons.setAlignment(Pos.CENTER);

        // FILLS WINDOW WITH MONTH INFORMATION
        GridPane gp = new GridPane();
        gp.addRow(0);

        FlowPane p;
        Label t;
        p = new FlowPane();
        t = new Label(months[date.get().getMonth()]);
        t.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.ITALIC, 30));
        p.getChildren().add(t);
        p.setAlignment(Pos.CENTER);
        t.setTextAlignment(TextAlignment.CENTER);
        centerPane.getChildren().add(p);
        centerPane.getChildren().add(buttons);
        centerPane.getChildren().add(gp);
        int i = 0;
        for (CalendarView.Days day : CalendarView.Days.values()) {
            p = new FlowPane();
            p.setAlignment(Pos.CENTER);
            t = new Label(day.toString());
            t.setTextAlignment(TextAlignment.CENTER);
            p.getChildren().add(t);
            p.setPrefWidth(100);
            gp.add(p, i, 0);
            i++;
        }
        YearMonth y = YearMonth.of(date.get().getYear(), date.get().getMonth() + 1);
        int startDay = getFirstDateOfMonth(date.get()).getDay();
        int rowCounter = 1;
        int columnCounter = startDay;
        for (int row = 2; row < 9; row++){
            gp.addRow(row);
        };
        for (i = 1; i <= y.lengthOfMonth(); i++) {
            p = new FlowPane();
            p.setMinHeight(150);
            p.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

            t = new Label("  " + Integer.toString(i));
            t.setFont(new Font(20));

            gp.add(p, columnCounter, rowCounter);
            columnCounter++;
            if (columnCounter == 7) {
                columnCounter = 0;
                rowCounter++;
            }

            // the following code displays the events to the calender
            VBox content = new VBox();
            content.setPadding(new Insets(5,5,5,5));
            content.setSpacing(5);
            content.getChildren().add(t);

            p.getChildren().add(content);

            for(CalendarController controller : c){
                // get the events from the current controller on the current date
                List<CalendarEvent> events = controller.getEventsOnDay(DayView.addToDate(getFirstDateOfMonth(date.get()), i-1));
                if(events != null){
                    for(CalendarEvent e : events){
                        Label l = new Label(e.getTitle() + " | " + controller.getName());
                        l.maxWidthProperty().bind(gp.widthProperty().divide(gp.impl_getColumnCount()).subtract(10));
                        HBox labelAndColor = new HBox(l);
                        labelAndColor.setBackground(new Background(new BackgroundFill(controller.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
                        labelAndColor.setPadding(new Insets(5,5,5,5));
                        labelAndColor.setOnMouseClicked((event)->{
                            ZoneId defaultZoneId = ZoneId.systemDefault();
                            Instant instant = e.getDate().toInstant();
                            AddEventModal m = new AddEventModal(false, e, controller, instant.atZone(defaultZoneId).toLocalDate());
                            m.show();
                        });
                        content.getChildren().add(labelAndColor);
                    }
                }
            }

        }
    }

    /**
     * Gets the first day of the month from the given date.
     *
     * @param date - date on which to find the first day of the month.
     * @return Which day the first of the month falls on.
     */
    private static Date getFirstDateOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * Adjusts the given date in order to reflect the changing of the month.
     *
     * @param date - date to be modified
     * @param amount - amount to modify the date by
     * @return modified date
     */
    private static Date addToDate(Date date, int amount){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, amount);
        return c.getTime();
    }
}
