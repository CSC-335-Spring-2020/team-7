package view;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class MonthView {
    private String[] months = {"January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"};

    public static Date getFirstDateOfMonth(Calendar cal) {
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    /**
     * Populates month view with initial values using the current date.
     */
    protected void setCenter(CalendarView calendarView, VBox centerPane) {
        //TODO: Integrate with model and controller so that dates are dynamic

        GridPane gp = new GridPane();
        centerPane.getChildren().add(gp);
        Calendar cal = Calendar.getInstance();
        gp.addRow(0);
        FlowPane p;
        Text t;
        p = new FlowPane();
        t = new Text(months[cal.getTime().getMonth()]);
        t.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.ITALIC, 30));
        p.getChildren().add(t);
        p.setAlignment(Pos.CENTER);
        t.setTextAlignment(TextAlignment.CENTER);
        centerPane.getChildren().add(p);
        int i = 0;
        for (CalendarView.Days day : CalendarView.Days.values()) {
            p = new FlowPane();
            //p.setMinWidth(100);
            p.setAlignment(Pos.CENTER);
            t = new Text(day.toString());
            t.setTextAlignment(TextAlignment.CENTER);
            p.getChildren().add(t);
            p.setPrefWidth(100);
            gp.add(p, i, 0);
            i++;
        }
        YearMonth y = YearMonth.of(cal.getTime().getYear(), cal.getTime().getMonth() + 1);
        System.out.println(y.getMonth());
        System.out.println(y.lengthOfMonth());
        int startDay = getFirstDateOfMonth(cal).getDay();
        //int lengthOfMonth = y.lengthOfMonth() + startDay;
        int rowCounter = 1;
        int columnCounter = startDay - 1;
        System.out.println(getFirstDateOfMonth(cal));
        for (int row = 2; row < 9; row++){ gp.addRow(row);};
        for (i = 1; i <= y.lengthOfMonth(); i++) {

            p = new FlowPane();
            // p.setMinWidth(100);
            p.setMinHeight(150);
            p.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
            t = new Text("  " + Integer.toString(i));
            t.setFont(new Font(20));
            p.getChildren().add(t);
            gp.add(p, columnCounter, rowCounter);
            columnCounter++;
            if (columnCounter == 7) {
                columnCounter = 0;
                rowCounter++;
            }
        }
    }
}
