package org.example.meetingplanner.viewmodel;

import javafx.application.Platform;
import javafx.beans.property.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.service.MeetingListService;

import java.time.LocalDate;
import java.util.Locale;

public class MeetingDetailViewModel {
    private static final Logger log = LogManager.getLogger(MeetingDetailViewModel.class);

    private final MeetingListService meetingListService;
    private final EventManager eventManager;

    private final ObjectProperty<LocalDate> from = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> to = new SimpleObjectProperty<>();
    private final StringProperty type = new SimpleStringProperty();

    private final DoubleProperty tourDistance = new SimpleDoubleProperty();
    private final DoubleProperty tourDuration = new SimpleDoubleProperty();

    private final StringProperty popularity = new SimpleStringProperty();
    private final StringProperty childFriendliness = new SimpleStringProperty();

    private final BooleanProperty tabPaneVisible = new SimpleBooleanProperty();

    private final BooleanProperty sendImage = new SimpleBooleanProperty();

    private final StringProperty locationProperty = new SimpleStringProperty();

    public MeetingDetailViewModel(EventManager eventManager, MeetingListService meetingListService) {
        this.meetingListService = meetingListService;
        this.eventManager = eventManager;
        eventManager.subscribe(Event.MEETING_SELECTED, this::onTourSelected);
        eventManager.subscribe(Event.MEETING_UPDATED, this::onTourSelected);
    }

    private void onTourSelected(Object s) {
        if (meetingListService.getSelectedMeeting() == null) {
            tabPaneVisible.set(false);
            return;
        }
        log.debug("Loading tour details");
        tabPaneVisible.set(true);
        from.setValue(meetingListService.getSelectedMeeting().getFrom());
        to.setValue(meetingListService.getSelectedMeeting().getTo());
    }

    public StringProperty popularityProperty() {
        return popularity;
    }

    public StringProperty childFriendlinessProperty() {
        return childFriendliness;
    }

    public Property<Boolean> tabPaneVisibleProperty() {
        return tabPaneVisible;
    }

    public StringProperty locationProperty() {
        return locationProperty;
    }

    public DoubleProperty tourDistanceProperty() {
        return tourDistance;
    }

    public DoubleProperty tourDurationProperty() {
        return tourDuration;
    }

    public BooleanProperty sendImageProperty() {
        return sendImage;
    }
}
