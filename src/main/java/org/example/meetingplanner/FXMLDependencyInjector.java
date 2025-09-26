package org.example.meetingplanner;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class FXMLDependencyInjector {

    private static final Logger log = LogManager.getLogger(FXMLDependencyInjector.class);

    public static Parent load(String location, Locale locale) throws IOException {
        log.debug("Loading FXML ({} / {})", location, locale);
        FXMLLoader loader = loader(location, locale);

        return loader.load();
    }

    public static FXMLLoader loader(String location, Locale locale) {
        return new FXMLLoader(
                FXMLDependencyInjector.class.getResource("/org/example/meetingplanner/" + location),
                ResourceBundle.getBundle("org.example.meetingplanner.i18n", locale),
                new JavaFXBuilderFactory(),
                viewClass -> ViewFactory.getInstance().create(viewClass)
        );
    }
}