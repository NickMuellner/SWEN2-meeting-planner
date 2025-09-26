package org.example.meetingplanner.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.viewmodel.MenuViewModel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MenuView implements Initializable {
    private static final Logger log = LogManager.getLogger(MenuView.class);
    private final MenuViewModel viewModel;

    private final FileChooser fileChooserPdf = new FileChooser();
    private final Alert alert = new Alert(Alert.AlertType.ERROR);

    @FXML
    private MenuBar menuBar;

    public MenuView(MenuViewModel menuViewModel) {
        this.viewModel = menuViewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize MenuView");

        fileChooserPdf.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    }

    @FXML
    public void meetingReport() {
        try {
            Window window = menuBar.getScene().getWindow();
            LocalDateTime now = LocalDateTime.now();
            String formattedDateTime = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss"));

            fileChooserPdf.setTitle("Meeting report");
            fileChooserPdf.setInitialFileName("Meeting report" + " " + formattedDateTime);

            fileChooserPdf.setInitialFileName("Meeting report" + " " + formattedDateTime);
            File selectedFile = fileChooserPdf.showSaveDialog(window);

            if (selectedFile != null) {
                viewModel.meetingReport(selectedFile);
                log.info("Meeting report exported to {}", selectedFile.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log.trace(sw);
            alert.setTitle("Error during Meeting Report");
            alert.setHeaderText("Meeting report could not be generated.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
