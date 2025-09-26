package org.example.meetingplanner.view;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.model.InvalidMeetingInput;
import org.example.meetingplanner.viewmodel.MeetingNoteCreateViewModel;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MeetingNoteCreateView implements Initializable {

    private static final Logger log = LogManager.getLogger(MeetingNoteCreateView.class);
    private final MeetingNoteCreateViewModel viewModel;

    @FXML
    private TextField comment, difficulty, distance, duration, rating;

    @FXML
    private DatePicker datetime;

    public MeetingNoteCreateView(MeetingNoteCreateViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize TourCreateView");

        comment.textProperty().bindBidirectional(viewModel.commentProperty());
        difficulty.textProperty().bindBidirectional(viewModel.difficultyProperty());
        distance.textProperty().bindBidirectional(viewModel.distanceProperty());
        duration.textProperty().bindBidirectional(viewModel.durationProperty());
        rating.textProperty().bindBidirectional(viewModel.ratingProperty());
        datetime.valueProperty().bindBidirectional(viewModel.datetimeProperty());
    }

    @FXML
    private void saveTourLog(ActionEvent event) {
        log.trace("Saving tour log");
        if (showInvalidInput(viewModel.createTourLog())) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    private boolean showInvalidInput(List<InvalidMeetingInput> invalidMeetingInput) {
        Map<Control, InvalidMeetingInput> inputs = Map.of(
                datetime, InvalidMeetingInput.INVALID_NOTE
        );
        inputs.forEach((key, invalidInput) -> {
            if (invalidMeetingInput.contains(invalidInput)) {
                key.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> key.setStyle(""));
                pause.play();
            }
        });

        return invalidMeetingInput.isEmpty();
    }
}
