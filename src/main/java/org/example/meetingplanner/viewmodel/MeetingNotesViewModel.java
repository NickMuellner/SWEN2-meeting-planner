package org.example.meetingplanner.viewmodel;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.model.MeetingUpdateStatus;
import org.example.meetingplanner.service.MeetingListService;

public class MeetingNotesViewModel {

    private final Property<ObservableList<MeetingNote>> tourLogs = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final BooleanProperty deleteVisibility = new SimpleBooleanProperty();
    private final Property<MeetingNote> selectedLog = new SimpleObjectProperty<>();
    private final BooleanProperty logStatusVisibleProperty = new SimpleBooleanProperty();
    private final StringProperty logStatusTextProperty = new SimpleStringProperty();
    private final Property<Color> logStatusColorProperty = new SimpleObjectProperty<>();
    private final DoubleProperty logStatusOpacityProperty = new SimpleDoubleProperty();
    private final PauseTransition logStatusPauseTransition = new PauseTransition(Duration.seconds(2));
    private final Property<FadeTransition> fadeTransitionProperty = new SimpleObjectProperty<>();
    private final MeetingListService meetingListService;

    public MeetingNotesViewModel(EventManager eventManager, MeetingListService meetingListService) {
        this.meetingListService = meetingListService;

        logStatusPauseTransition.setOnFinished(event -> fadeTransitionProperty.getValue().play());

        eventManager.subscribe(Event.MEETING_SELECTED, this::refreshLogs);
        eventManager.subscribe(Event.MEETING_NOTE_CREATED, this::addedLog);
        selectedLog.addListener((observable, oldValue, newValue) ->
                deleteVisibility.setValue(newValue != null));
    }

    private void refreshLogs(Object s) {
        if (meetingListService.getSelectedMeeting() != null) {
            tourLogs.getValue().setAll(meetingListService.getSelectedMeeting().getNotes());
        }
    }

    public Property<ObservableList<MeetingNote>> tourLogs() {
        return tourLogs;
    }

    public void addedLog(Object s) {
        refreshLogs("");
        displayStatus(MeetingUpdateStatus.SUCCESS);
    }

    public BooleanProperty deleteVisibility() {
        return deleteVisibility;
    }

    public Property<MeetingNote> selectedLog() {
        return selectedLog;
    }

    public void deleteLog() {
        meetingListService.removeMeetingNote(selectedLog.getValue());
        refreshLogs("");
        displayStatus(MeetingUpdateStatus.SUCCESS);
    }

    public void updateLog(MeetingNote entry) {
        meetingListService.updateMeetingNote(entry);
        displayStatus(MeetingUpdateStatus.SUCCESS);
    }

    public void displayStatus(MeetingUpdateStatus status) {
        logStatusPauseTransition.stop();
        fadeTransitionProperty.getValue().stop();
        logStatusColorProperty.setValue(status.getColor());
        logStatusTextProperty.setValue(status.getValue());
        logStatusVisibleProperty.setValue(true);
        logStatusOpacityProperty.setValue(1);
        logStatusPauseTransition.play();
    }

    public BooleanProperty logStatusVisibleProperty() {
        return logStatusVisibleProperty;
    }

    public StringProperty logStatusTextProperty() {
        return logStatusTextProperty;
    }

    public Property<Color> logStatusColorProperty() {
        return logStatusColorProperty;
    }

    public DoubleProperty logStatusOpacityProperty() {
        return logStatusOpacityProperty;
    }

    public Property<FadeTransition> fadeTransitionProperty() {
        return fadeTransitionProperty;
    }
}
