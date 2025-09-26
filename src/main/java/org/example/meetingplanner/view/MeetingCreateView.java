package org.example.meetingplanner.view;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.model.InvalidMeetingInput;
import org.example.meetingplanner.viewmodel.MeetingCreateViewModel;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MeetingCreateView implements Initializable {

    private static final Logger log = LogManager.getLogger(MeetingCreateView.class);
    private final MeetingCreateViewModel viewModel;
    private boolean isPopupMode = false;

    @FXML
    private TextField name, description, from, to, distance, time, information;

    @FXML
    private CheckBox favorite;

    @FXML
    private ComboBox<String> type;

    public MeetingCreateView(MeetingCreateViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize TourCreateView");

        name.textProperty().bindBidirectional(viewModel.nameProperty());
        description.textProperty().bindBidirectional(viewModel.descriptionProperty());
        from.textProperty().bindBidirectional(viewModel.fromProperty());
        to.textProperty().bindBidirectional(viewModel.toProperty());
        viewModel.typeProperty().setValue(type.getValue());
        information.textProperty().bindBidirectional(viewModel.informationProperty());
        favorite.selectedProperty().bindBidirectional(viewModel.favoriteProperty());

        viewModel.distanceProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                distance.setText(convertDistance(newVal));
            } else {
                distance.setText("");
            }
        });

        viewModel.timeProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                time.setText(convertDuration(newVal));
            } else {
                time.setText("");
            }
        });

        viewModel.typeProperty().addListener((obs, oldType, newType) -> {
            if (newType != null && !newType.equals(type.getSelectionModel().getSelectedItem())) {
                type.getSelectionModel().select(newType);
            }
        });

        type.getSelectionModel().selectedItemProperty().addListener((obs, oldType, newType) -> {
            if (newType != null && !newType.equals(viewModel.typeProperty().getValue())) {
                viewModel.typeProperty().setValue(newType);
            }
        });
    }

    public void setPopupMode(boolean popupMode) {
        this.isPopupMode = popupMode;
    }

    private String convertDistance(String s) {
        double meters = Double.parseDouble(s);
        int km = (int) (meters / 1000);
        int m = (int) (meters % 1000);

        if (km > 0) {
            return String.format("%d km %d m", km, m);
        } else {
            return String.format("%d m", m);
        }
    }

    private String convertDuration(String s) {
        int totalSeconds = (int) Double.parseDouble(s);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d h %d min %d s", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d min %d s", minutes, seconds);
        } else {
            return String.format("%d s", seconds);
        }
    }

    @FXML
    private void saveTour(ActionEvent event) {
        log.trace("Saving tour");
        if (isPopupMode) {
            if (showInvalidInput(viewModel.createTour())) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        } else {
            showInvalidInput(viewModel.updateTour());
        }
    }

    private boolean showInvalidInput(List<InvalidMeetingInput> invalidMeetingInput) {
        Map<TextField, InvalidMeetingInput> inputs = Map.of(
                name, InvalidMeetingInput.INVALID_TITLE,
                from, InvalidMeetingInput.INVALID_FROM,
                to, InvalidMeetingInput.INVALID_TO
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
