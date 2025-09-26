package org.example.meetingplanner.view;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.FXMLDependencyInjector;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.viewmodel.MeetingListViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class MeetingListView implements Initializable {

    private static final Logger log = LogManager.getLogger(MeetingListView.class);
    private final MeetingListViewModel viewModel;

    @FXML
    private ListView<Meeting> meetingList;

    @FXML
    private Button deleteMeeting;

    @FXML
    private Label meetingStatus;

    public MeetingListView(MeetingListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize MeetingListView");

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), meetingStatus);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> meetingStatus.setVisible(false));

        viewModel.meetings().bindBidirectional(meetingList.itemsProperty());
        deleteMeeting.visibleProperty().bindBidirectional(viewModel.deleteMeetingVisibleProperty());
        viewModel.meetingStatusVisibleProperty().bindBidirectional(meetingStatus.visibleProperty());
        viewModel.meetingStatusTextProperty().bindBidirectional(meetingStatus.textProperty());
        meetingStatus.textFillProperty().bind(viewModel.meetingStatusColorProperty());
        viewModel.meetingStatusOpacityProperty().bindBidirectional(meetingStatus.opacityProperty());
        viewModel.fadeTransitionProperty().setValue(fadeOut);

        viewModel.selectedMeeting().addListener((obs, oldMeeting, newMeeting) -> {
            if (newMeeting != null && !newMeeting.equals(meetingList.getSelectionModel().getSelectedItem())) {
                meetingList.getSelectionModel().select(newMeeting);
            }
        });

        meetingList.getSelectionModel().selectedItemProperty().addListener((obs, oldMeeting, newMeeting) -> {
            if (newMeeting != null && !newMeeting.equals(viewModel.selectedMeeting().getValue())) {
                viewModel.selectedMeeting().setValue(newMeeting);
            }
        });
    }

    @FXML
    public void addMeeting() throws IOException {
        log.trace("Adding Meeting");
        FXMLLoader loader = FXMLDependencyInjector.loader(
                "meeting-manage-view.fxml",
                Locale.ENGLISH
        );
        Parent root = loader.load();
        MeetingManageView controller = loader.getController();
        controller.setPopupMode(true);
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(root));
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    @FXML
    public void deleteMeeting() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this Meeting?");
        alert.setContentText("This action cannot be undone.");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            log.trace("Deleting Meeting");
            viewModel.deleteMeeting();
        } else {
            log.trace("Meeting deletion cancelled by user.");
        }
    }
}