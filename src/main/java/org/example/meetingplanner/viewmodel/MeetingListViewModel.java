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

import java.time.format.DateTimeFormatter;

public class MeetingListViewModel {

    private final MeetingListService meetingListService;

    private final Property<ObservableList<Meeting>> meetings = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final Property<Meeting> selectedMeeting = new SimpleObjectProperty<>();

    private final BooleanProperty deleteMeetingVisible = new SimpleBooleanProperty();

    private final BooleanProperty meetingStatusVisibleProperty = new SimpleBooleanProperty();
    private final StringProperty meetingStatusTextProperty = new SimpleStringProperty();
    private final Property<Color> meetingStatusColorProperty = new SimpleObjectProperty<>();
    private final DoubleProperty meetingStatusOpacityProperty = new SimpleDoubleProperty();

    private final PauseTransition meetingStatusPauseTransition = new PauseTransition(Duration.seconds(2));
    private final Property<FadeTransition> fadeTransitionProperty = new SimpleObjectProperty<>();

    public MeetingListViewModel(EventManager eventManager, MeetingListService meetingListService) {
        this.meetingListService = meetingListService;

        meetingStatusPauseTransition.setOnFinished(event -> fadeTransitionProperty.getValue().play());

        selectedMeeting.addListener((obs, oldValue, newValue) -> {
            meetingListService.selectMeeting(newValue);
            deleteMeetingVisible.setValue(newValue != null);
        });
        eventManager.subscribe(Event.MEETING_UPDATED, this::onMeetingUpdated);
        eventManager.subscribe(Event.MEETING_CREATED, this::onMeetingUpdated);
        eventManager.subscribe(Event.MEETING_CREATED, this::selectMeeting);
        eventManager.subscribe(Event.MEETING_UPDATED_FAILED, this::onMeetingUpdatedFailed);
        eventManager.subscribe(Event.SEARCH_STARTED, this::search);

        Platform.runLater(() -> meetings.getValue().setAll(meetingListService.getMeetings()));
    }

    private void onMeetingUpdated(Object s) {
        String status = (String) s;
        meetings.getValue().setAll(meetingListService.getMeetings());
        displayStatus(MeetingUpdateStatus.valueOf(status.toUpperCase()));
    }

    private void onMeetingUpdatedFailed(Object s) {
        String status = (String) s;
        displayStatus(MeetingUpdateStatus.valueOf(status.toUpperCase()));
    }

    private void search(Object s) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        String search = (String) s;
        meetings.getValue().setAll(meetingListService.getMeetings().stream().filter(
                e -> e.getTitle().contains(search) ||
                        e.getFrom().format(dateTimeFormatter).contains(search) ||
                        e.getTo().format(dateTimeFormatter).contains(search) ||
                        e.getAgenda().contains(search) ||
                        e.getNotes().stream().anyMatch(
                                l -> l.getNote().contains(search)
                        )
        ).toList());
        if (!meetings.getValue().contains(meetingListService.getSelectedMeeting()) && meetingListService.getSelectedMeeting() != null) {
            selectedMeeting.setValue(null);
            meetingListService.deselectMeeting();
        }
        displayStatus(MeetingUpdateStatus.SEARCHED);
    }

    public Property<ObservableList<Meeting>> meetings() {
        return meetings;
    }

    public Property<Meeting> selectedMeeting() {
        return selectedMeeting;
    }

    public void deleteMeeting() {
        meetingListService.removeMeeting(selectedMeeting.getValue());
        meetings.getValue().setAll(meetingListService.getMeetings());
        deleteMeetingVisible.setValue(false);
        displayStatus(MeetingUpdateStatus.SUCCESS);
    }

    private void selectMeeting(Object s) {
        selectedMeeting.setValue(meetingListService.getSelectedMeeting());
    }

    public BooleanProperty deleteMeetingVisibleProperty() {
        return deleteMeetingVisible;
    }

    public BooleanProperty meetingStatusVisibleProperty() {
        return meetingStatusVisibleProperty;
    }

    public Property<String> meetingStatusTextProperty() {
        return meetingStatusTextProperty;
    }

    public Property<Color> meetingStatusColorProperty() {
        return meetingStatusColorProperty;
    }

    private void displayStatus(MeetingUpdateStatus status) {
        meetingStatusPauseTransition.stop();
        fadeTransitionProperty.getValue().stop();
        meetingStatusColorProperty.setValue(status.getColor());
        meetingStatusTextProperty.setValue(status.getValue());
        meetingStatusVisibleProperty.setValue(true);
        meetingStatusOpacityProperty.setValue(1);
        meetingStatusPauseTransition.play();
    }

    public Property<FadeTransition> fadeTransitionProperty() {
        return fadeTransitionProperty;
    }

    public DoubleProperty meetingStatusOpacityProperty() {
        return meetingStatusOpacityProperty;
    }
}
