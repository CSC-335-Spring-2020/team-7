package view;

import controller.CalendarController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CalendarEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * This modal can be used to add, edit, and display events, described in
 * detail in the constructor
 *
 * @author Amin Sennour
 */

public class AddEventModal extends Stage {

    // this boolean is set false by default and set true only when something
    // is changed. This way, if nothing is changed, the modal can just close
    // rather than creating a new event every time
    private boolean changed;

    /**
     * Exists so that lambda functions can change "changed"
     * @param newVal the new value
     */
    private void setWasChanged(boolean newVal){
        changed = newVal;
    }

    /**
     * Constructor for the modal, used for adding, viewing and editing
     * events.
     *
     * Two use cases.
     * 1. Add event. Pass true for editable and null for the event
     * 2. View / edit event. Pass false for editable and the event for event
     *
     * @param editable this boolean indicates if the modal is editable
     *                 by default
     * @param event this is the event, pass null if your'e creating a new
     *              event, else the values from this will be used to populate
     *              the modal, allowing for viewership, and this event will
     *              be removed from the model if the user chooses to edit
     */
    public AddEventModal(boolean editable, CalendarEvent event, CalendarController c){
        // used to position the fields
        Pos position = Pos.BASELINE_LEFT;
        // used to determine the max width of the fields
        double maxWidth = Double.MAX_VALUE;

        // setup
        initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Add Event");
        BorderPane bp = new BorderPane();
        bp.setPrefSize(300, 600);

        // common padding used
        Insets allSidesPadded = new Insets(15);
        Insets bottomPadded = new Insets(0,0,15,0);

        // configure the Vbox that makes of the top layer of the
        // center of the modal
        VBox vb = new VBox();
        vb.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        vb.setAlignment(Pos.CENTER);
        vb.setFillWidth(true);
        vb.setPadding(allSidesPadded);


        /*
         * Code for the Title label and text box
         */
        TextField title = new TextField(event == null ? "Enter Event Title" : event.getTitle());
        title.setOnKeyTyped((e)->setWasChanged(editable));
        title.setMaxWidth(maxWidth);
        title.setEditable(editable);

        Label titleLabel = new Label("Event Title");
        titleLabel.setLabelFor(title);
        titleLabel.setFont(new Font(15));
        titleLabel.setAlignment(Pos.CENTER);

        VBox titleAndLabel = new VBox(titleLabel, title);
        titleAndLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        titleAndLabel.setAlignment(position);
        titleAndLabel.setFillWidth(true);

        HBox titleBox = new HBox(titleAndLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(bottomPadded);
        HBox.setHgrow(titleAndLabel, Priority.ALWAYS);


        /*
         * Code for the start Date label and date picker
         */
        DatePicker startDate = new DatePicker(java.time.LocalDate.now());
        startDate.setOnAction((e)->setWasChanged(editable));
        startDate.setMaxWidth(maxWidth);
        startDate.setDisable(!editable);
        startDate.setStyle("-fx-opacity: 1;");
        startDate.getEditor().setStyle("-fx-opacity: 1;");

        Label startDateLabel = new Label("Start Date");
        startDateLabel.setLabelFor(startDate);
        startDateLabel.setFont(new Font(15));
        startDateLabel.setAlignment(Pos.CENTER);

        VBox startDateAndLabel = new VBox(startDateLabel, startDate);
        startDateAndLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        startDateAndLabel.setAlignment(position);
        startDateAndLabel.setFillWidth(true);

        HBox startDateBox = new HBox(startDateAndLabel);
        startDateBox.setAlignment(Pos.CENTER);
        startDateBox.setPadding(bottomPadded);
        HBox.setHgrow(startDateAndLabel, Priority.ALWAYS);


        /*
         * Code for the start time selector
         */
        Spinner<Integer> startTime =
                new Spinner<>( new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        startTime.setDisable(!editable);
        startTime.setStyle("-fx-opacity: 1;");
        startTime.getEditor().setStyle("-fx-opacity: 1;");
        startTime.setPrefWidth(55);
        startTime.setMinWidth(55);

        Label startTimeLabel = new Label("Start Time");
        startTimeLabel.setLabelFor(startTime);
        startTimeLabel.setFont(new Font(15));
        startTimeLabel.setAlignment(Pos.CENTER);

        VBox startTimeAndLabel = new VBox(startTimeLabel, startTime);
        startTimeAndLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        startTimeAndLabel.setAlignment(position);
        startTimeAndLabel.setFillWidth(true);

        HBox startTimeBox = new HBox(startTimeAndLabel);
        startTimeBox.setAlignment(Pos.CENTER);
        startTimeBox.setPadding(bottomPadded);
        HBox.setHgrow(startTimeAndLabel, Priority.ALWAYS);


        /*
         * Code for the end time selector
         */
        Spinner<Integer> endTime =
                new Spinner<>( new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        endTime.setDisable(!editable);
        endTime.setStyle("-fx-opacity: 1;");
        endTime.getEditor().setStyle("-fx-opacity: 1;");
        endTime.setPrefWidth(55);
        endTime.setMinWidth(55);

        Label endTimeLabel = new Label("End Time");
        endTimeLabel.setLabelFor(endTime);
        endTimeLabel.setFont(new Font(15));
        endTimeLabel.setAlignment(Pos.CENTER);

        VBox endTimeAndLabel = new VBox(endTimeLabel, endTime);
        endTimeAndLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        endTimeAndLabel.setAlignment(position);
        endTimeAndLabel.setFillWidth(true);

        HBox endTimeBox = new HBox(endTimeAndLabel);
        endTimeBox.setAlignment(Pos.CENTER);
        endTimeBox.setPadding(bottomPadded);
        HBox.setHgrow(endTimeAndLabel, Priority.ALWAYS);


        /*
         * Code for the Location label and text box
         */
        TextField location = new TextField(event == null ? "Enter Location" : event.getLocation());
        location.setOnKeyTyped((e)->setWasChanged(editable));
        location.setMaxWidth(maxWidth);
        location.setEditable(editable);

        Label locationLabel = new Label("Location");
        locationLabel.setLabelFor(location);
        locationLabel.setFont(new Font(15));
        locationLabel.setAlignment(Pos.CENTER);

        VBox locationAndLabel = new VBox(locationLabel, location);
        locationAndLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        locationAndLabel.setAlignment(position);
        locationAndLabel.setFillWidth(true);

        HBox locationBox = new HBox(locationAndLabel);
        locationBox.setAlignment(Pos.CENTER);
        locationBox.setPadding(bottomPadded);
        HBox.setHgrow(locationAndLabel, Priority.ALWAYS);


        /*
         * Code for the notes label and text area
         */
        TextArea notes = new TextArea(event == null ? "Enter Notes" : event.getNotes());
        notes.setOnKeyTyped((e)->setWasChanged(editable));
        notes.setMaxWidth(maxWidth);
        notes.setPrefHeight(300);
        notes.setEditable(editable);

        Label notesLabel = new Label("Notes");
        notesLabel.setLabelFor(notes);
        notesLabel.setFont(new Font(15));
        notesLabel.setAlignment(Pos.CENTER);

        VBox notesAndLabel = new VBox(notesLabel, notes);
        notesAndLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        notesAndLabel.setAlignment(Pos.CENTER_LEFT);
        notesAndLabel.setFillWidth(true);

        HBox notesBox = new HBox(notesAndLabel);
        notesBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(notesAndLabel, Priority.ALWAYS);


        /*
         * Code for the Right event button, by default this is the "save event"
         * button, it will delete an edited event / create a new event, or
         * just close the modal
         */
        Button rightEventButton = new Button("Save Event");
        rightEventButton.setOnMouseClicked((e)->{

            Date date = localDateAndHourToDate(startDate.getValue(), 0);

            Date newStart = localDateAndHourToDate(
                    startDate.getValue(), startTime.getValue());
            Date newEnd = localDateAndHourToDate(
                    startDate.getValue(), endTime.getValue());

            if(event != null &&  changed){
                c.removeEvent(event);
            }

            if(changed){
                c.addEvent(
                        title.getText(), date, newStart, newEnd,
                        location.getText(), notes.getText());
            }

            // print out diagnostic information
            System.out.println("Title : " + title.getText());
            System.out.println("Start Date : " + newStart);
            System.out.println("End Date : " + newEnd);
            System.out.println("Location : " + location.getText());
            System.out.println("Notes :\n" + notes.getText());
            this.close();
        });
        rightEventButton.setMaxWidth(Double.MAX_VALUE);

        /*
         * Code for the Left event button, sets the event as editable,
         * does this by launching a new modal and closing this one
         */
        Button leftEventButton = new Button("Edit Event");
        leftEventButton.setOnMouseClicked(e->{
            AddEventModal m = new AddEventModal(true, event, c);
            m.show();
            this.close();
        });
        leftEventButton.setMaxWidth(Double.MAX_VALUE);

        /*
         * Code for the bottom buttons box, add or remove from the list to
         * change the buttons
         */
        ArrayList<Button> buttons =
                new ArrayList<>(Arrays.asList(leftEventButton, rightEventButton));

        HBox buttonBox = new HBox();
        for(Button b : buttons){
            buttonBox.getChildren().add(b);
            HBox.setHgrow(b, Priority.ALWAYS);
        }
        buttonBox.setAlignment(Pos.BASELINE_CENTER);
        buttonBox.setPadding(new Insets(0,15,15,15));


        // put everything in the VBox
        vb.getChildren().add(titleBox);
        vb.getChildren().add(startDateBox);
        vb.getChildren().add(startTimeBox);
        vb.getChildren().add(endTimeBox);
        vb.getChildren().add(locationBox);
        vb.getChildren().add(notesBox);

        bp.setCenter(vb);
        bp.setBottom(buttonBox);

        Scene scene = new Scene(bp);
        this.setScene(scene);
    }

    /**
     * Handles the logic of converting the information from the local date
     * and hour integer into a java "Date" object
     * @param date the local date being converted
     * @param hour the hour on the local date to also be converted
     * @return the new java "date" representation of the above info
     */
    private Date localDateAndHourToDate(LocalDate date, int hour){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateString = date.toString();
        dateString += " " + hour + ":00:00";

        Date ret = null;
        try {
            ret = formatter.parse(dateString);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        return ret;
    }

}
