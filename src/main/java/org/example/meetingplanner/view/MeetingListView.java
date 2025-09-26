package org.example.meetingplanner.view;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
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
    private ListView<Meeting> tourList;

    @FXML
    private Button deleteTour;

    @FXML
    private ToggleButton favoriteToggle;

    @FXML
    private Label tourStatus;

    public MeetingListView(MeetingListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize TourListView");

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), tourStatus);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> tourStatus.setVisible(false));

        viewModel.tours().bindBidirectional(tourList.itemsProperty());
        deleteTour.visibleProperty().bindBidirectional(viewModel.deleteTourVisibleProperty());
        viewModel.tourStatusVisibleProperty().bindBidirectional(tourStatus.visibleProperty());
        viewModel.tourStatusTextProperty().bindBidirectional(tourStatus.textProperty());
        tourStatus.textFillProperty().bind(viewModel.tourStatusColorProperty());
        viewModel.tourStatusOpacityProperty().bindBidirectional(tourStatus.opacityProperty());
        viewModel.fadeTransitionProperty().setValue(fadeOut);
        favoriteToggle.selectedProperty().bindBidirectional(viewModel.favoriteProperty());
        favoriteToggle.textProperty().bind(Bindings.createStringBinding(
                () -> favoriteToggle.isSelected() ? "★" : "☆",
                favoriteToggle.selectedProperty()));

        viewModel.selectedTour().addListener((obs, oldTour, newTour) -> {
            if (newTour != null && !newTour.equals(tourList.getSelectionModel().getSelectedItem())) {
                tourList.getSelectionModel().select(newTour);
            }
        });

        tourList.getSelectionModel().selectedItemProperty().addListener((obs, oldTour, newTour) -> {
            if (newTour != null && !newTour.equals(viewModel.selectedTour().getValue())) {
                viewModel.selectedTour().setValue(newTour);
            }
        });
    }

    @FXML
    public void addTour() throws IOException {
        log.trace("Adding tour");
        FXMLLoader loader = FXMLDependencyInjector.loader(
                "meeting-create-view.fxml",
                Locale.ENGLISH
        );
        Parent root = loader.load();
        MeetingCreateView controller = loader.getController();
        controller.setPopupMode(true);
        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(root));
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    }

    @FXML
    public void deleteTour() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this tour?");
        alert.setContentText("This action cannot be undone.");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            log.trace("Deleting tour");
            viewModel.deleteTour();
        } else {
            log.trace("Tour deletion cancelled by user.");
        }
    }

    @FXML
    private void toggleFavorite() {
        log.trace("Toggling favorite");
        viewModel.toggleFavorite();
    }
}