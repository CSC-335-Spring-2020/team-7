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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author Nicholas Lindenberg
 * Current revision VIEW_2 Month UI
 * Month UI statically implemented, need to be able to change between months.
 */
public class CalendarView extends javafx.application.Application implements Observer {
    private BorderPane bp;
    protected VBox centerPane;
    private Scene scene;

    // had to make this global so it could be changed based on the
    // arrows in the different views
    DatePicker startDate = new DatePicker(java.time.LocalDate.now());

    // this has been updated to a list of controllers, for when we implant
    // multiple calenders
    protected List<CalendarController> c = new ArrayList<>();
    private CalendarModel m;

    // the current date, exact for day view, week of for week view, and month of for month view
    AtomicReference<Date> date = new AtomicReference<>(WeekView.zeroOutTime(new Date()));

    // used by week view, unsure what for
    Date[] datesOfWeek = new Date[7];

    // used for keeping track of the current view
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
        c.add(new CalendarController(m));
        bp = new BorderPane();

        setCenter();
        sideBarUI();
        scene = new Scene(bp);
        primaryStage.setTitle(String.format("%s's Calendar", c.get(0).getName()));

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
            System.out.println("Set to the month view");
            MonthView.setCenter(this,centerPane);
        }
        if (currView == 2){
            WeekView.setCenter(this,c,date,datesOfWeek,centerPane);
        }
        if (currView == 3){
            DayView.setCenter(this,c,date,centerPane);
        }
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

        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = date.get().toInstant();

        startDate.setValue(instant.atZone(defaultZoneId).toLocalDate());
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

        startDate.setOnAction((e)->{
            date.set(AddEventModal.localDateAndHourToDate(startDate.getValue(), 0));
            update(null, null);
        });

        /*
         * Following code is for the radio button set for switching between
         * the views
         */
        ToggleGroup viewButtonGroup = new ToggleGroup();

        RadioButton monthViewButton = new RadioButton("Month");
        monthViewButton.setToggleGroup(viewButtonGroup);
        monthViewButton.setSelected(true);
        monthViewButton.setOnAction(event -> {
            currView = 1;
            setCenter();
        });

        RadioButton weekViewButton = new RadioButton("Week");
        weekViewButton.setToggleGroup(viewButtonGroup);
        weekViewButton.setOnAction(event -> {
            currView = 2;
            setCenter();
        });

        RadioButton dayViewButton = new RadioButton("Day");
        dayViewButton.setToggleGroup(viewButtonGroup);
        dayViewButton.setOnAction(event -> {
            currView = 3;
            setCenter();
        });

        VBox viewHBox = new VBox(10,monthViewButton, weekViewButton,dayViewButton);

        Button addEventButton = new Button("Add Event");
        addEventButton.setOnAction(event -> {
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Instant instant = date.get().toInstant();
            AddEventModal modalInstance = new AddEventModal(true,null,c.get(0), instant.atZone(defaultZoneId).toLocalDate());
            modalInstance.show();
        });


        VBox v = new VBox(20,setDayHBox,startDate,viewHBox,addEventButton);
        v.setPadding(new Insets(15,15,15,15));
        bp.setLeft(v);
    }
}
