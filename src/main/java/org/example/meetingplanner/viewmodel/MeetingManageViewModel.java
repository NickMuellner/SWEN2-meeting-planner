package org.example.meetingplanner.viewmodel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.InvalidMeetingInput;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.model.MeetingUpdateStatus;
import org.example.meetingplanner.service.MeetingListService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeetingManageViewModel {
    private static final Logger log = LogManager.getLogger(MeetingManageViewModel.class);

    private final MeetingListService meetingListService;
    private final EventManager eventManager;

    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fromDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> toDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> fromTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> toTime = new SimpleObjectProperty<>();
    private final StringProperty agenda = new SimpleStringProperty();
    private final ObservableList<MeetingNote> notes = FXCollections.observableArrayList();

    public MeetingManageViewModel(EventManager eventManager, MeetingListService meetingListService) {
        this.meetingListService = meetingListService;
        this.eventManager = eventManager;
        eventManager.subscribe(Event.MEETING_SELECTED, this::onMeetingSelected);
    }

    private void onMeetingSelected(Object s) {
        if (meetingListService.getSelectedMeeting() == null) {
            return;
        }
        log.debug("Loading meeting details");
        title.setValue(meetingListService.getSelectedMeeting().getTitle());
        fromDate.setValue(meetingListService.getSelectedMeeting().getFrom().toLocalDate());
        toDate.setValue(meetingListService.getSelectedMeeting().getFrom().toLocalDate());
        fromTime.setValue(meetingListService.getSelectedMeeting().getTo().toLocalTime());
        toTime.setValue(meetingListService.getSelectedMeeting().getTo().toLocalTime());
        agenda.setValue(meetingListService.getSelectedMeeting().getAgenda());
        notes.setAll(meetingListService.getSelectedMeeting().getNotes());
    }

    public StringProperty titleProperty() {
        return title;
    }

    public ObjectProperty<LocalDate> fromDateProperty() {
        return fromDate;
    }

    public ObjectProperty<LocalDate> toDateProperty() {
        return toDate;
    }

    public ObjectProperty<LocalTime> fromTimeProperty() {
        return fromTime;
    }

    public ObjectProperty<LocalTime> toTimeProperty() {
        return toTime;
    }

    public StringProperty agendaProperty() {
        return agenda;
    }

    public ObservableList<MeetingNote> notesProperty() {
        return notes;
    }

    private List<InvalidMeetingInput> manageMeeting(boolean isNewMeeting) {
        MeetingUpdateStatus status = MeetingUpdateStatus.SUCCESS;
        List<InvalidMeetingInput> invalidMeetingInput = new ArrayList<>();

        Map<StringProperty, InvalidMeetingInput> inputs = Map.of(
                title, InvalidMeetingInput.INVALID_TITLE
                //from, InvalidMeetingInput.INVALID_FROM,
                //to, InvalidMeetingInput.INVALID_TO todo
        );
        inputs.forEach((key, error) -> {
            if (key.getValue() == null || key.getValue().trim().isEmpty()) {
                invalidMeetingInput.add(error);
            }
        });

        if (invalidMeetingInput.isEmpty()) {
            if (isNewMeeting) {
                meetingListService.addMeeting(new Meeting(title.get(), LocalDateTime.of(fromDate.get(), fromTime.get()), LocalDateTime.of(toDate.get(), toTime.get()), agenda.get()));
                meetingListService.updateMeetingNotes(new ArrayList<>(notes));
            } else {
                meetingListService.updateMeeting(title.get(), LocalDateTime.of(fromDate.get(), fromTime.get()), LocalDateTime.of(toDate.get(), toTime.get()), agenda.get());
                meetingListService.updateMeetingNotes(new ArrayList<>(notes));
            }
            eventManager.publish(isNewMeeting ? Event.MEETING_CREATED : Event.MEETING_UPDATED, status.getValue());
            log.info(isNewMeeting ? "Crating Meeting succeeded" : "Updating Meeting succeeded");
        } else {
            status = MeetingUpdateStatus.FAILURE;
            eventManager.publish(isNewMeeting ? Event.MEETING_CREATED_FAILED : Event.MEETING_UPDATED_FAILED, status.getValue());
            log.warn(isNewMeeting ? "Crating Meeting failed" : "Updating Meeting failed");
        }

        return invalidMeetingInput;
    }

    public List<InvalidMeetingInput> createMeeting() {
        return manageMeeting(true);
    }

    public List<InvalidMeetingInput> updateMeeting() {
        return manageMeeting(false);
    }
}