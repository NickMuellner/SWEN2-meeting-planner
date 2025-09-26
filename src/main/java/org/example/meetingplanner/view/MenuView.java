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
import java.util.function.Consumer;

public class MenuView implements Initializable {
    private static final Logger log = LogManager.getLogger(MenuView.class);
    private final MenuViewModel viewModel;

    private final FileChooser fileChooserPdf = new FileChooser();
    private final FileChooser fileChooserJson = new FileChooser();
    private final Alert alert = new Alert(Alert.AlertType.ERROR);

    @FXML
    private MenuBar menuBar;

    public MenuView(MenuViewModel menuViewModel) {
        this.viewModel = menuViewModel;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize MenuView");

        fileChooserJson.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
        fileChooserPdf.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    }

    @FXML
    public void exportFile() {
        createFileChooser("Tour export", true, Type.JSON, file -> {
            try {
                viewModel.exportFile(file);
                log.info("File exported to {}", file.getAbsolutePath());
            } catch (IOException e) {
                showError("Error during Export", "File could not be exported.", e);
            }
        });
    }

    @FXML
    public void importFile() {
        createFileChooser("Tour import", false, Type.JSON, file -> {
            try {
                viewModel.importFile(file);
                log.info("File imported from {}", file.getAbsolutePath());
            } catch (IOException e) {
                showError("Error during Import", "File could not be imported.", e);
            }
        });
    }

    @FXML
    public void tourReport() {
        createFileChooser("Tour report", true, Type.PDF, file -> {
            try {
                viewModel.tourReport(file);
                log.info("Tour report exported to {}", file.getAbsolutePath());
            } catch (RuntimeException e) {
                showError("Error during Report", "Tour report could not be generated.", e);
            }
        });
    }

    @FXML
    public void summarizeReport() {
        createFileChooser("Summarize report", true, Type.PDF, file -> {
            try {
                viewModel.meetingReport(file);
                log.info("Summarize report exported to {}", file.getAbsolutePath());
            } catch (IOException e) {
                showError("Error during Report", "Summarize report could not be generated.", e);
            }
        });
    }

    private void createFileChooser(String title, boolean isSavingFile, Type type, Consumer<File> action) {
        Window window = menuBar.getScene().getWindow();
        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss"));

        File selectedFile = switch (type) {
            case Type.JSON -> {
                fileChooserJson.setTitle(title);
                if (isSavingFile) {
                    fileChooserJson.setInitialFileName(title + " " + formattedDateTime);
                    yield fileChooserJson.showSaveDialog(window);
                }
                yield fileChooserJson.showOpenDialog(window);
            }
            case Type.PDF -> {
                fileChooserPdf.setTitle(title);
                fileChooserPdf.setInitialFileName(title + " " + formattedDateTime);

                if (isSavingFile) {
                    fileChooserPdf.setInitialFileName(title + " " + formattedDateTime);
                    yield fileChooserPdf.showSaveDialog(window);
                }
                yield fileChooserPdf.showOpenDialog(window);
            }
        };
        if (selectedFile != null) {
            action.accept(selectedFile);
        }
    }

    private void showError(String title, String header, Exception exception) {
        log.error(exception.getMessage());
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        log.trace(sw);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    private enum Type {
        PDF,
        JSON
    }
}
