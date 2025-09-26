package org.example.meetingplanner.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.InvalidMeetingInput;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.model.MeetingUpdateStatus;
import org.example.meetingplanner.service.MeetingListService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeetingNoteCreateViewModel {
    private static final Logger log = LogManager.getLogger(MeetingNoteCreateViewModel.class);

    private final MeetingListService meetingListService;
    private final EventManager eventManager;

    private final StringProperty comment = new SimpleStringProperty();
    private final StringProperty difficulty = new SimpleStringProperty();
    private final StringProperty distance = new SimpleStringProperty();
    private final StringProperty duration = new SimpleStringProperty();
    private final StringProperty rating = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> datetime = new SimpleObjectProperty<>();

    public MeetingNoteCreateViewModel(EventManager eventManager, MeetingListService meetingListService) {
        this.meetingListService = meetingListService;
        this.eventManager = eventManager;
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public StringProperty difficultyProperty() {
        return difficulty;
    }

    public StringProperty distanceProperty() {
        return distance;
    }

    public StringProperty durationProperty() {
        return duration;
    }

    public StringProperty ratingProperty() {
        return rating;
    }

    public ObjectProperty<LocalDate> datetimeProperty() {
        return datetime;
    }

    public List<InvalidMeetingInput> createTourLog() {
        MeetingUpdateStatus status = MeetingUpdateStatus.SUCCESS;
        List<InvalidMeetingInput> invalidMeetingInput = new ArrayList<>();

        Map<StringProperty, InvalidMeetingInput> inputs = Map.of(
                distance, InvalidMeetingInput.INVALID_NOTE
        );

        inputs.forEach((key, error) -> {
            if (key.getValue() == null || key.getValue().trim().isEmpty()) {
                invalidMeetingInput.add(error);
            }
        });


        if (invalidMeetingInput.isEmpty()) {
            meetingListService.addMeetingNote(new MeetingNote(
                    comment.get()
            ));

            eventManager.publish(Event.MEETING_NOTE_CREATED, status.getValue());
            log.info("Crating tour log succeeded");
        }

        return invalidMeetingInput;
    }
}
