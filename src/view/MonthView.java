package view;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class MonthView extends CalendarView {

    protected static void setCenter(CalendarView calendarView, VBox centerPane, GridPane gp) {
        //TODO: Integrate with model and controller so that dates are dynamic

        calendarView.resetCenter();
        centerPane.getChildren().add(gp);

        gp.addRow(0);
        FlowPane p;
        Text t;
        p = new FlowPane();
        t = new Text("January\n");
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
        int dayCounter = 1;
        for (i = 2; i < 7; i++) {
            gp.addRow(i);
            for (int j = 0; j < 7; j++) {
                p = new FlowPane();
                // p.setMinWidth(100);
                p.setMinHeight(150);
                p.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
                if (dayCounter < 31) {
                    t = new Text("  " + Integer.toString(dayCounter));
                } else {
                    t = new Text("  " + Integer.toString(dayCounter - 30));
                    t.setFill(Color.GRAY);
                }
                t.setFont(new Font(20));
                p.getChildren().add(t);
                dayCounter++;
                gp.add(p, j, i);
            }
        }
    }
}
