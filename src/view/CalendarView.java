package view;

import controller.CalendarController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.CalendarModel;

import java.awt.*;

public class CalendarView extends javafx.application.Application {
    private BorderPane bp;
    private GridPane gp;
    private Scene scene;
    private CalendarController c;
    private CalendarModel m;

    private enum Days {
        Sunday,
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: Add and set icon for calendar window
        //primaryStage.setMinHeight(1000);
        primaryStage.setMaxWidth(1400);
        m = new CalendarModel("TestCalendar");
        c = new CalendarController(m);
        gp = new GridPane();
        bp = new BorderPane();
        setCenter();
        scene = new Scene(bp);
        primaryStage.setTitle(String.format("%s's Calendar", c.getName()));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Populates month view
     */
    private void setCenter() {
        //TODO: Integrate with model and controller so that dates are dynamic
        gp.addRow(0);
        FlowPane p;
        Text t;
        int i = 0;
        for (Days day : Days.values()) {
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
        for (i = 1; i < 6; i++) {
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
        bp.setCenter(gp);
    }

    /**
     * Populates Side bar UI
     */
    private void sideBarUI() {
        //TODO: Add event button
        //TODO: Display upcoming events
        VBox v = new VBox();
        bp.setLeft(v);
    }
}
