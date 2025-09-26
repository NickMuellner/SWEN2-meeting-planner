package org.example.meetingplanner.view;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.FXMLDependencyInjector;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.model.MeetingUpdateStatus;
import org.example.meetingplanner.viewmodel.MeetingNotesViewModel;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class MeetingNotesView implements Initializable {

    private static final Logger log = LogManager.getLogger(MeetingNotesView.class);
    private final MeetingNotesViewModel viewmodel;

    @FXML
    private TableView<MeetingNote> logs;

    @FXML
    private TableColumn<MeetingNote, String> note;

    @FXML
    private Button deleteLog;

    @FXML
    private Label logStatus;

    public MeetingNotesView(MeetingNotesViewModel viewmodel) {
        this.viewmodel = viewmodel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize TourLogsView");

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), logStatus);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> logStatus.setVisible(false));

        note.setCellValueFactory(new PropertyValueFactory<>("note"));

        logs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        note.setCellFactory(TextFieldTableCell.forTableColumn());

        viewmodel.deleteVisibility().bindBidirectional(deleteLog.visibleProperty());
        viewmodel.tourLogs().bindBidirectional(logs.itemsProperty());
        viewmodel.selectedLog().bind(logs.getSelectionModel().selectedItemProperty());

        viewmodel.logStatusVisibleProperty().bindBidirectional(logStatus.visibleProperty());
        viewmodel.logStatusTextProperty().bindBidirectional(logStatus.textProperty());
        logStatus.textFillProperty().bind(viewmodel.logStatusColorProperty());
        viewmodel.logStatusOpacityProperty().bindBidirectional(logStatus.opacityProperty());
        viewmodel.fadeTransitionProperty().setValue(fadeOut);
    }

    private void initCellFactory(TableColumn<MeetingNote, Integer> column) {
        column.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return value != null ? value.toString() : "";
            }

            @Override
            public Integer fromString(String text) {
                try {
                    viewmodel.displayStatus(MeetingUpdateStatus.SUCCESS);
                    return Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    viewmodel.displayStatus(MeetingUpdateStatus.FAILURE);
                    return 0;
                }
            }
        }));
    }

    private void initCellFactory(TableColumn<MeetingNote, Double> column, String sufix) {
        column.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Double value) {
                return value != null ? value + " " + sufix : "";
            }

            @Override
            public Double fromString(String text) {
                try {
                    viewmodel.displayStatus(MeetingUpdateStatus.SUCCESS);
                    return Double.parseDouble(text.replace(sufix, ""));
                } catch (NumberFormatException e) {
                    viewmodel.displayStatus(MeetingUpdateStatus.FAILURE);
                    return 0.0;
                }
            }
        }));
    }

    @FXML
    private void addLog() throws IOException {
        log.trace("Adding tour log");
        Parent root = FXMLDependencyInjector.load(
                "meeting-note-create-view.fxml",
                Locale.ENGLISH
        );
        Stage popupStage = new Stage();
        popupStage.setTitle("Create Tour log");
        popupStage.setScene(new Scene(root));
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    @FXML
    private void deleteLog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this log?");
        alert.setContentText("This action cannot be undone.");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            log.trace("Deleting tour log");
            viewmodel.deleteLog();
        } else {
            log.trace("Log deletion cancelled by user.");
        }
    }

    private static class DatePickerTableCell extends TableCell<MeetingNote, LocalDate> {
        private final DatePicker datePicker = new DatePicker();
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        public DatePickerTableCell() {
            datePicker.setEditable(false);
            datePicker.setOnAction(event -> {
                commitEdit(datePicker.getValue());
                cancelEdit();
            });
            datePicker.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    if (getItem() != null && !datePicker.getValue().equals(getItem())) {
                        commitEdit(datePicker.getValue());
                    } else {
                        cancelEdit();
                    }
                }
            });
            this.setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (getItem() == null) return;
            datePicker.setValue(getItem());
            setGraphic(datePicker);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            datePicker.show();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem() != null ? getItem().format(formatter) : "");
            setGraphic(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        protected void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                datePicker.setValue(item);
                setGraphic(datePicker);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(item != null ? item.format(formatter) : "");
                setGraphic(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }
}
