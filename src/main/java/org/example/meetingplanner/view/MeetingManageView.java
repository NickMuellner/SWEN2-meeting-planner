package org.example.meetingplanner.view;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.model.InvalidMeetingInput;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.viewmodel.MeetingManageViewModel;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MeetingManageView implements Initializable {

    private static final Logger log = LogManager.getLogger(MeetingManageView.class);
    private final MeetingManageViewModel viewModel;
    private boolean isPopupMode = false;

    @FXML
    private TextField title;

    @FXML
    private TextArea agenda;

    @FXML
    private DatePicker fromDate, toDate;

    @FXML
    private Spinner<LocalTime> fromTime, toTime;

    @FXML
    private ListView<MeetingNote> notes;

    @FXML
    private TextField newNote;

    public MeetingManageView(MeetingManageViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize MeetingManageView");
        SpinnerValueFactory<LocalTime> fromTimeFactory = createSpinnerValueFactory();
        SpinnerValueFactory<LocalTime> toTimeFactory = createSpinnerValueFactory();

        fromTime.setValueFactory(fromTimeFactory);
        toTime.setValueFactory(toTimeFactory);
        createConverter(fromDate);
        createConverter(toDate);

        title.textProperty().bindBidirectional(viewModel.titleProperty());
        fromDate.valueProperty().bindBidirectional(viewModel.fromDateProperty());
        toDate.valueProperty().bindBidirectional(viewModel.toDateProperty());
        fromTime.getValueFactory().valueProperty().bindBidirectional(viewModel.fromTimeProperty());
        toTime.getValueFactory().valueProperty().bindBidirectional(viewModel.toTimeProperty());
        agenda.textProperty().bindBidirectional(viewModel.agendaProperty());
        notes.setItems(viewModel.notesProperty());

        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        fromDate.setValue(nowDate);
        toDate.setValue(nowDate);
        fromTimeFactory.setValue(nowTime);
        toTimeFactory.setValue(nowTime.plusHours(1));
    }

    private void createConverter(DatePicker toDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        toDate.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });
    }

    public void setPopupMode(boolean popupMode) {
        this.isPopupMode = popupMode;
    }

    @FXML
    private void saveMeeting(ActionEvent event) {
        log.trace("Saving Meeting");
        if (isPopupMode) {
            if (showInvalidInput(viewModel.createMeeting())) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        } else {
            showInvalidInput(viewModel.updateMeeting());
        }
    }

    private boolean showInvalidInput(List<InvalidMeetingInput> invalidMeetingInput) {
        Map<TextInputControl, InvalidMeetingInput> inputsString = Map.of(
                title, InvalidMeetingInput.INVALID_TITLE
        );
        Map<DatePicker, InvalidMeetingInput> inputsDate = Map.of(
                fromDate, InvalidMeetingInput.INVALID_DATE,
                toDate, InvalidMeetingInput.INVALID_DATE
        );
        Map<Spinner<LocalTime>, InvalidMeetingInput> inputsTime = Map.of(
                fromTime, InvalidMeetingInput.INVALID_TIME,
                toTime, InvalidMeetingInput.INVALID_TIME
        );
        checkInvalidInput(invalidMeetingInput, inputsString);
        checkInvalidInput(invalidMeetingInput, inputsDate);
        checkInvalidInput(invalidMeetingInput, inputsTime);

        return invalidMeetingInput.isEmpty();
    }

    private void checkInvalidInput(List<InvalidMeetingInput> invalidMeetingInput, Map<? extends Control, InvalidMeetingInput> input) {
        input.forEach((key, invalidInput) -> {
            if (invalidMeetingInput.contains(invalidInput)) {
                key.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> key.setStyle(""));
                pause.play();
            }
        });
    }

    @FXML
    private void addNote() {
        if (newNote.getText().trim().isEmpty()) {
            newNote.setStyle("-fx-border-color: red; -fx-border-width: 1;");
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> newNote.setStyle(""));
            pause.play();
        } else {
            notes.getItems().add(new MeetingNote(newNote.getText().trim()));
            newNote.setText("");
        }
    }

    @FXML
    private void deleteNote() {
        notes.getItems().remove(notes.getSelectionModel().getSelectedItem());
    }

    private SpinnerValueFactory<LocalTime> createSpinnerValueFactory() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return new SpinnerValueFactory<>() {
            {
                setConverter(new LocalTimeStringConverter(formatter, null));
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps));
            }
        };
    }
}
