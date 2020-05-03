package view;

import controller.CalendarController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import model.CalendarModel;

import java.util.Calendar;
import java.util.Date;


/**
 * @author Nicholas Lindenberg
 * Current revision VIEW_2 Month UI
 * Month UI statically implemented, need to be able to change between months.
 */
public class CalendarView extends javafx.application.Application {
    private BorderPane bp;
    private GridPane gp;
    protected VBox centerPane;
    private Scene scene;
    protected CalendarController c;
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

        // this code was moved from the setCenter method so that
        // only that method needed to change for the inheritance
        // to work
        centerPane = new VBox();
        bp.setCenter(centerPane);
        setCenter();

        sideBarUI();
        scene = new Scene(bp);
        primaryStage.setTitle(String.format("%s's Calendar", c.getName()));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Populates month view
     */
    protected void setCenter() {
        //TODO: Integrate with model and controller so that dates are dynamic
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
        centerPane.getChildren().add(gp);
    }

    /**
     * Populates Side bar UI
     */
    private void sideBarUI() {
        //TODO: Add event button
        //TODO: Display upcoming events
        Calendar now = Calendar.getInstance();
        Label setDayLabel = new Label("Enter date (MM/DD/YYYY)");
        HBox setDayHBox = new HBox(setDayLabel);
        setDayHBox.setAlignment(Pos.CENTER);
        TextField monthField = new TextField(String.valueOf(now.get(Calendar.MONTH) + 1));
        TextField dayField = new TextField(String.valueOf(now.get(Calendar.DATE)));
        TextField yearField = new TextField(String.valueOf(now.get(Calendar.YEAR)));
        monthField.setPrefWidth(40);
        dayField.setPrefWidth(40);
        yearField.setPrefWidth(60);
        HBox dateHBox = new HBox(monthField,dayField,yearField);

        RadioButton monthViewButton = new RadioButton("Month");
        RadioButton weekViewButton = new RadioButton("Week");
        RadioButton dayViewButton = new RadioButton("Day");
        ToggleGroup viewButtonGroup = new ToggleGroup();
        monthViewButton.setToggleGroup(viewButtonGroup);
        monthViewButton.setSelected(true);
        weekViewButton.setToggleGroup(viewButtonGroup);
        dayViewButton.setToggleGroup(viewButtonGroup);
        VBox viewHBox = new VBox(10,monthViewButton, weekViewButton,dayViewButton);
        Button addEventButton = new Button("Add Event");
        addEventButton.setOnAction(event -> {
            AddEventModal modalInstance = new AddEventModal(true,null,c);
            modalInstance.show();
        });


        VBox v = new VBox(20,setDayHBox,dateHBox,viewHBox,addEventButton);
        bp.setLeft(v);
    }
}
