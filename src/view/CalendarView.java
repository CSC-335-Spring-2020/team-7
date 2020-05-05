package view;

import controller.CalendarController;
import javafx.geometry.Insets;
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
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author Nicholas Lindenberg
 * Current revision VIEW_2 Month UI
 * Month UI statically implemented, need to be able to change between months.
 */
public class CalendarView extends javafx.application.Application implements Observer {
    private BorderPane bp;
    private GridPane gp;
    protected VBox centerPane;
    private Scene scene;
    protected CalendarController c;
    private CalendarModel m;
    AtomicReference<Date> date = new AtomicReference<>(WeekView.zeroOutTime(new Date()));
    Date[] datesOfWeek = new Date[7];
    int currView = 1; // 1= monthView, 2 = weekView, 3 = dayView

    enum Days {
        Sunday,
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday
    }

    /**
     * This method should be called in all setCenter methods, including in
     * children, as the first line, so that redrawing works
     */
    public void resetCenter(){
        centerPane = new VBox();
        centerPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        bp.setCenter(centerPane);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: Add and set icon for calendar window
        //primaryStage.setMinHeight(1000);
        // why is the width maxed?
        //primaryStage.setMaxWidth(1400);
        primaryStage.setMaximized(true);
        m = new CalendarModel("TestCalendar");
        m.addObserver(this);
        c = new CalendarController(m);
        gp = new GridPane();
        bp = new BorderPane();

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

        resetCenter();
        if (currView == 1){
            MonthView.setCenter(this,centerPane,gp);
        }
        if (currView == 2){
            WeekView.setCenter(this,c,date,datesOfWeek,centerPane);
        }
        if (currView == 3){
            DayView.setCenter(this,c,date,centerPane);
        }
        bp.setCenter(centerPane);
    }

    /**
     * This method will be called whenever something in the model
     * is updated default implementation just calls set center again
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("update");
        setCenter();
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
        DatePicker startDate = new DatePicker(java.time.LocalDate.now());


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
        dayViewButton.setOnAction(event -> {
            currView = 3;
            setCenter();
        });
        weekViewButton.setOnAction(event -> {
            currView = 2;
            setCenter();
        });
        monthViewButton.setOnAction(event -> {
            currView = 1;
            setCenter();
        });
        VBox v = new VBox(20,setDayHBox,startDate,viewHBox,addEventButton);
        bp.setLeft(v);
    }
}
