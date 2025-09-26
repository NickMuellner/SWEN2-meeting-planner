package org.example.meetingplanner.viewmodel;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingUpdateStatus;
import org.example.meetingplanner.service.MeetingListService;

public class MeetingListViewModel {

    private final MeetingListService meetingListService;

    private final Property<ObservableList<Meeting>> tours = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final Property<Meeting> selectedTour = new SimpleObjectProperty<>();
    private final BooleanProperty favoriteProperty = new SimpleBooleanProperty();

    private final BooleanProperty deleteTourVisible = new SimpleBooleanProperty();

    private final BooleanProperty tourStatusVisibleProperty = new SimpleBooleanProperty();
    private final StringProperty tourStatusTextProperty = new SimpleStringProperty();
    private final Property<Color> tourStatusColorProperty = new SimpleObjectProperty<>();
    private final DoubleProperty tourStatusOpacityProperty = new SimpleDoubleProperty();

    private final PauseTransition tourStatusPauseTransition = new PauseTransition(Duration.seconds(2));
    private final Property<FadeTransition> fadeTransitionProperty = new SimpleObjectProperty<>();

    public MeetingListViewModel(EventManager eventManager, MeetingListService meetingListService) {
        this.meetingListService = meetingListService;

        tourStatusPauseTransition.setOnFinished(event -> fadeTransitionProperty.getValue().play());

        selectedTour.addListener((obs, oldValue, newValue) -> {
            meetingListService.selectTour(newValue);
            deleteTourVisible.setValue(newValue != null);
        });
        eventManager.subscribe(Event.MEETING_UPDATED, this::onTourUpdated);
        eventManager.subscribe(Event.MEETING_CREATED, this::onTourUpdated);
        eventManager.subscribe(Event.MEETING_CREATED, this::selectTour);
        eventManager.subscribe(Event.MEETING_UPDATED_FAILED, this::onTourUpdatedFailed);
        eventManager.subscribe(Event.SEARCH_STARTED, this::search);

        Platform.runLater(() -> tours.getValue().setAll(meetingListService.getMeetings()));
    }

    private void exportCompleted(Object s) {
        displayStatus(MeetingUpdateStatus.SUCCESS);
    }

    private void importCompleted(Object s) {
        displayStatus(MeetingUpdateStatus.SUCCESS);
        tours.getValue().setAll(meetingListService.getMeetings());
    }

    private void onTourUpdated(Object s) {
        String status = (String) s;
        tours.getValue().setAll(meetingListService.getMeetings());
        displayStatus(MeetingUpdateStatus.valueOf(status.toUpperCase()));
    }

    private void onTourUpdatedFailed(Object s) {
        String status = (String) s;
        displayStatus(MeetingUpdateStatus.valueOf(status.toUpperCase()));
    }

    private void search(Object s) {
        String search = (String) s;
        tours.getValue().setAll(meetingListService.getMeetings().stream().filter(
                e -> e.getTitle().contains(search) ||
                        e.getFrom().toString().contains(search) ||
                        e.getTo().toString().contains(search) ||
                        e.getAgenda().contains(search) ||
                        e.getNotes().stream().anyMatch(
                                l -> l.getNote().contains(search)
                        )
        ).toList());
        if (!tours.getValue().contains(meetingListService.getSelectedMeeting()) && meetingListService.getSelectedMeeting() != null) {
            meetingListService.deselectMeeting();
        }
        displayStatus(MeetingUpdateStatus.SEARCHED);
    }

    public Property<ObservableList<Meeting>> tours() {
        return tours;
    }

    public Property<Meeting> selectedTour() {
        return selectedTour;
    }

    public void deleteTour() {
        meetingListService.removeMeeting(selectedTour.getValue());
        tours.getValue().setAll(meetingListService.getMeetings());
        displayStatus(MeetingUpdateStatus.SUCCESS);
    }

    private void selectTour(Object s) {
        selectedTour.setValue(meetingListService.getSelectedMeeting());
    }

    public BooleanProperty deleteTourVisibleProperty() {
        return deleteTourVisible;
    }

    public BooleanProperty tourStatusVisibleProperty() {
        return tourStatusVisibleProperty;
    }

    public Property<String> tourStatusTextProperty() {
        return tourStatusTextProperty;
    }

    public Property<Color> tourStatusColorProperty() {
        return tourStatusColorProperty;
    }

    private void displayStatus(MeetingUpdateStatus status) {
        tourStatusPauseTransition.stop();
        fadeTransitionProperty.getValue().stop();
        tourStatusColorProperty.setValue(status.getColor());
        tourStatusTextProperty.setValue(status.getValue());
        tourStatusVisibleProperty.setValue(true);
        tourStatusOpacityProperty.setValue(1);
        tourStatusPauseTransition.play();
    }

    public Property<FadeTransition> fadeTransitionProperty() {
        return fadeTransitionProperty;
    }

    public DoubleProperty tourStatusOpacityProperty() {
        return tourStatusOpacityProperty;
    }

    public BooleanProperty favoriteProperty() {
        return favoriteProperty;
    }

    public void toggleFavorite() {
        boolean fav = !favoriteProperty.get();
        favoriteProperty.set(fav);
    }
}
