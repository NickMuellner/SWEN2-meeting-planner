package org.example.meetingplanner.view;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
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
        log.trace("Initialize TourCreateView");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        SpinnerValueFactory<LocalTime> fromTimeFactory = new SpinnerValueFactory<LocalTime>() {
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
        SpinnerValueFactory<LocalTime> toTimeFactory = new SpinnerValueFactory<LocalTime>() {
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

        fromTime.setValueFactory(fromTimeFactory);
        toTime.setValueFactory(toTimeFactory);

        title.textProperty().bindBidirectional(viewModel.titleProperty());
        fromDate.valueProperty().bindBidirectional(viewModel.fromDateProperty());
        toDate.valueProperty().bindBidirectional(viewModel.toDateProperty());
        fromTime.getValueFactory().valueProperty().bindBidirectional(viewModel.fromTimeProperty());
        toTime.getValueFactory().valueProperty().bindBidirectional(viewModel.toTimeProperty());
        agenda.textProperty().bindBidirectional(viewModel.agendaProperty());
        notes.setItems(viewModel.notesProperty());

        fromDate.setValue(LocalDate.now());
        toDate.setValue(LocalDate.now());
        fromTimeFactory.setValue(LocalTime.now());
        toTimeFactory.setValue(LocalTime.now().plusHours(1));
    }

    public void setPopupMode(boolean popupMode) {
        this.isPopupMode = popupMode;
    }

    @FXML
    private void saveMeeting(ActionEvent event) {
        log.trace("Saving tour");
        if (isPopupMode) {
            if (showInvalidInput(viewModel.createMeeting())) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        } else {
            showInvalidInput(viewModel.updateTour());
        }
    }

    private boolean showInvalidInput(List<InvalidMeetingInput> invalidMeetingInput) {
        Map<TextInputControl, InvalidMeetingInput> inputsString = Map.of(
                title, InvalidMeetingInput.INVALID_TITLE,
                agenda, InvalidMeetingInput.INVALID_AGENDA
        );
        Map<DatePicker, InvalidMeetingInput> inputsDate = Map.of(
                fromDate, InvalidMeetingInput.INVALID_FROM,
                toDate, InvalidMeetingInput.INVALID_TO
        );
        Map<Spinner<LocalTime>, InvalidMeetingInput> inputsTime = Map.of(
                fromTime, InvalidMeetingInput.INVALID_FROM,
                toTime, InvalidMeetingInput.INVALID_TO
        );
        inputsString.forEach((key, invalidInput) -> {
            if (invalidMeetingInput.contains(invalidInput)) {
                key.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> key.setStyle(""));
                pause.play();
            }
        });
        inputsDate.forEach((key, invalidInput) -> {
            if (invalidMeetingInput.contains(invalidInput)) {
                key.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> key.setStyle(""));
                pause.play();
            }
        });
        inputsTime.forEach((key, invalidInput) -> {
            if (invalidMeetingInput.contains(invalidInput)) {
                key.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> key.setStyle(""));
                pause.play();
            }
        });

        return invalidMeetingInput.isEmpty();
    }

    @FXML
    private void addNote() {
        if(newNote.getText().trim().isEmpty()) {
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
}
