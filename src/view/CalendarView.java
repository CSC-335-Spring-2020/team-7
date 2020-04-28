package view;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CalendarView extends javafx.application.Application {
    private BorderPane bp;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO: Add and set icon for calendar window
        bp = new BorderPane();
        scene = new Scene(bp);
        primaryStage.setTitle("[ User ]'s Calendar");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Populates month view
     */
    private void setCenter(){
        VBox v = new VBox();



        bp.setLeft(v);
    }

    /**
     * Populates Side bar UI
     */
    private void sideBarUI(){
        //TODO: Add event button
        //TODO: Display upcoming events
    }
}
