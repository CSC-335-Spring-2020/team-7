package view;

import controller.CalendarController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CalendarModel;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This modal can be used to add new "calendars" to the user's collection
 * this is also where adding calendar files to the collection will take place
 *
 * @author Amin Sennour
 */

public class AddCalendarModal extends Stage {

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
     */
    public AddCalendarModal(CalendarView view, boolean editable, List<CalendarController> c){
        // used to position the fields
        Pos position = Pos.BASELINE_LEFT;
        // used to determine the max width of the fields
        double maxWidth = Double.MAX_VALUE;

        // setup
        initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Add Event");
        BorderPane bp = new BorderPane();
        bp.setPrefSize(300, 400);

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
        TextField title = new TextField("Name");
        title.setMaxWidth(maxWidth);
        title.setEditable(editable);

        Label titleLabel = new Label("Name your Calendar");
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
         * Code to Pick the color
         */
        ColorPicker hold = new ColorPicker();
        hold.getStyleClass().add("button");
        hold.setMaxWidth(Double.MAX_VALUE);
        HBox color = new HBox(hold);
        color.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox.setHgrow(hold, Priority.ALWAYS);

        Label colorLabel = new Label("Color");
        colorLabel.setLabelFor(color);
        colorLabel.setFont(new Font(15));
        colorLabel.setAlignment(Pos.CENTER);

        VBox colorAndLabel = new VBox(colorLabel, color);
        colorAndLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        colorAndLabel.setFillWidth(true);

        HBox colorBox = new HBox(colorAndLabel);
        colorBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        colorBox.setAlignment(position);
        colorBox.setPadding(bottomPadded);
        HBox.setHgrow(colorAndLabel, Priority.ALWAYS);

        /*
         * Code for loading a model from a file
         */
        Label gotFileLabel = new Label();
        gotFileLabel.setLabelFor(gotFileLabel);
        gotFileLabel.setFont(new Font(15));
        gotFileLabel.setAlignment(Pos.CENTER);

        // holder for the file the filer picker will get
        AtomicReference<File> gotFile = new AtomicReference<>();

        Button file = new Button("File");
        file.setMaxWidth(maxWidth);

        Label fileLabel = new Label("Load from a File");
        fileLabel.setLabelFor(file);
        fileLabel.setFont(new Font(15));
        fileLabel.setAlignment(Pos.CENTER);

        VBox fileAndLabel = new VBox(fileLabel, file);
        fileAndLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        fileAndLabel.setFillWidth(true);

        HBox fileBox = new HBox(fileAndLabel);
        fileBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        fileBox.setAlignment(position);
        fileBox.setPadding(bottomPadded);
        HBox.setHgrow(fileAndLabel, Priority.ALWAYS);

        file.setOnMouseClicked((e)->{
            FileChooser f = new FileChooser();
            f.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("ICS", "*.ics")
            );
            f.setTitle("Open Resource File");
            gotFile.set(f.showOpenDialog(view.stage));
            if(gotFile.get() != null){
                gotFileLabel.setText(gotFile.get().getName() + "  ✔");
                fileAndLabel.getChildren().add(gotFileLabel);
            }
        });

        /*
         * Code for the Right event button, by default this is the "save event"
         * button, it will delete an edited event / create a new event, or
         * just close the modal
         */
        Button rightEventButton = new Button("Save Calendar");
        rightEventButton.setOnMouseClicked((e)->{
            CalendarModel m = null;
            if(gotFile.get() != null){
                // TODO code for loading a .ics file into a model
            }else{
                m = new CalendarModel(title.getText(), hold.getValue());
            }
            m.addObserver(view);
            c.add(new CalendarController(m));
            view.update(null, null);
            this.close();
        });
        rightEventButton.setMaxWidth(Double.MAX_VALUE);

        HBox buttonBox = new HBox(rightEventButton);
        buttonBox.setAlignment(Pos.BASELINE_CENTER);
        buttonBox.setPadding(new Insets(0,15,15,15));
        HBox.setHgrow(rightEventButton, Priority.ALWAYS);


        // put everything in the VBox
        vb.getChildren().add(titleBox);
        vb.getChildren().add(colorBox);
        vb.getChildren().add(fileBox);

        bp.setCenter(vb);
        bp.setBottom(buttonBox);

        Scene scene = new Scene(bp);
        this.setScene(scene);
    }
}
