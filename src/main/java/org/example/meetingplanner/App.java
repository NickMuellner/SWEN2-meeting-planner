package org.example.meetingplanner;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.config.JpaManager;

import java.io.IOException;
import java.util.Locale;

public class App extends Application {
    private static final Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        log.info("Launching application");
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        log.info("Loading stage");
        Parent mainView = FXMLDependencyInjector.load(
                "main-view.fxml",
                Locale.ENGLISH
        );
        Scene scene = new Scene(mainView);
        stage.setTitle("Meeting Planner");
        stage.setScene(scene);
        stage.show();
        log.info("Loading complete");
    }

    @Override
    public void stop() {
        log.info("Closing application");
        JpaManager.close();
    }
}