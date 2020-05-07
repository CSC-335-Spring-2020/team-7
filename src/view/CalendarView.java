package view;

import controller.CalendarAutoSave;
import controller.CalendarController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.CalendarModel;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


/**
 * This class constructs the view and handles what is displayed and passes user inputs
 * into the controller.
 * @author Nicholas Lindenberg
 * @author Mahmood Gladney
 */
public class CalendarView extends javafx.application.Application implements Observer {
    private BorderPane bp;
    protected VBox centerPane;
    private Scene scene;

    protected Stage stage;

    // had to make this global so it could be changed based on the
    // arrows in the different views
    DatePicker startDate = new DatePicker(java.time.LocalDate.now());

    // this has been updated to a list of controllers, for when we implant
    // multiple calenders
    protected List<CalendarController> c;

    // the calender currently being viewed, altered by the sideBarUI
    private CalendarController currentController;

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

    /**
     * Start method, loads up the application
     *
     * @param primaryStage the primary stage
     * @throws Exception if something in javaFX goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: Add and set icon for calendar window
        //primaryStage.setMinHeight(1000);
        // why is the width maxed?
        //primaryStage.setMaxWidth(1400);
        primaryStage.setMaximized(true);
        stage = primaryStage;

        //TODO set this to load a users calender rather than a default empty one

        c = CalendarAutoSave.getSavedCalendars(this);
        CalendarAutoSave.launchAutoSave(c);

        if(c.isEmpty()){
            CalendarModel m = new CalendarModel("Default", Color.OLDLACE);
            m.addObserver(this);
            c.add(new CalendarController(m));
        }

        currentController = c.get(0);

        bp = new BorderPane();

        setCenter();
        sideBarUI();
        scene = new Scene(bp);
        primaryStage.setTitle(String.format("%s's Calendar", currentController.getName()));
        primaryStage.setOnCloseRequest((e)->{
            CalendarAutoSave.closeAutoSave();
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Populates month view based on the value of "currView" used to
     * switch between the month, week, and day views
     */
    protected void setCenter() {
        //TODO: Integrate with model and controller so that dates are dynamic
        resetCenter();
        if (currView == 1){
            MonthView.setCenter(this, c, date,centerPane);
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
     * @param o the data. we don't use this
     * @param arg some argument. we don't use this
     */
    @Override
    public void update(Observable o, Object arg) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = date.get().toInstant();

        startDate.setValue(instant.atZone(defaultZoneId).toLocalDate());
        setCenter();
        sideBarUI();
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

            // following logic checks if a calender exists or not, only launches the modal if one does
            if(currentController == null && c.size() <= 0){
                // the user has no calendars, alert them and close
                Alert noCal = new Alert(Alert.AlertType.WARNING);
                noCal.setHeaderText("Error");
                noCal.setContentText("You have no calendars to add to.\n" +
                        "Please create a new calendar.");
                noCal.showAndWait();
                return;
            }else if(currentController == null && !c.isEmpty()){
                // currentController is unset, but their exists a controller
                currentController = c.get(0);
            }

            AddEventModal modalInstance = new AddEventModal(true,null,currentController, instant.atZone(defaultZoneId).toLocalDate());
            modalInstance.show();
        });
        addEventButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox v = new VBox(20,setDayHBox,startDate,viewHBox,addEventButton);

        // using a border pane so the logic with adding / removing calenders
        // sticks to the bottom
        BorderPane sideBar = new BorderPane();
        sideBar.setPadding(new Insets(15,15,15,15));
        sideBar.setCenter(v);

        /*
         * The following code is for the adding and removing of
         * calendars
         */
        Button addCalenders = new Button("Add a new Calender");
        addCalenders.setOnMouseClicked((e)->{
            AddCalendarModal m = new AddCalendarModal(this, true, c);
            m.show();
        });
        addCalenders.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button saveCalendars = new Button("Save current Calendar");
        saveCalendars.setOnMouseClicked((e)->{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Calendar");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("ICS", "*.ics")
            );
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                String filePath = file.getAbsolutePath();
                try {
                    CalendarController.exportCalendarToFile(currentController.model, filePath);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        saveCalendars.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Label switchLabel = new Label("Choose your Calendar");
        ChoiceBox<CalendarController> switchCalenders = new ChoiceBox<>();
        switchCalenders.setValue(currentController);
        switchCalenders.setOnAction((e)->{
            currentController = switchCalenders.getValue();
        });
        for(CalendarController controller : c){
            switchCalenders.getItems().add(controller);
        }
        switchCalenders.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Button removeCalenders = new Button("Remove");
        removeCalenders.setOnMouseClicked((e)->{
            Alert x = new Alert(Alert.AlertType.WARNING);
            x.setTitle("Warning");
            x.setHeaderText("Are you sure?");
            x.showAndWait();

            c.remove(currentController);
            currentController = null;
            update(null, null);
        });
        removeCalenders.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox switchAndRemove = new HBox(switchCalenders, removeCalenders);

        VBox labelAndSwitch = new VBox(switchLabel, switchAndRemove);
        labelAndSwitch.setSpacing(5);

        VBox addSwitchCalenders = new VBox(addCalenders, saveCalendars, labelAndSwitch);
        addSwitchCalenders.setSpacing(15);
        sideBar.setBottom(addSwitchCalenders);

        bp.setLeft(sideBar);
    }
}
