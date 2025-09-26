package org.example.meetingplanner.viewmodel;

import javafx.beans.property.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.InvalidMeetingInput;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingUpdateStatus;
import org.example.meetingplanner.service.MeetingListService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeetingCreateViewModel {
    private static final Logger log = LogManager.getLogger(MeetingCreateViewModel.class);

    private final MeetingListService meetingListService;
    private final EventManager eventManager;

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> from = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> to = new SimpleObjectProperty<>();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty distance = new SimpleStringProperty();
    private final StringProperty time = new SimpleStringProperty();
    private final StringProperty information = new SimpleStringProperty();
    private final BooleanProperty favorite = new SimpleBooleanProperty();

    public MeetingCreateViewModel(EventManager eventManager, MeetingListService meetingListService) {
        this.meetingListService = meetingListService;
        this.eventManager = eventManager;
        eventManager.subscribe(Event.MEETING_SELECTED, this::onMeetingSelected);
    }

    private void onMeetingSelected(Object s) {
        if (meetingListService.getSelectedMeeting() == null) {
            return;
        }
        log.debug("Loading tour details");
        name.setValue(meetingListService.getSelectedMeeting().getTitle());
        description.setValue(meetingListService.getSelectedMeeting().getAgenda());
        from.setValue(meetingListService.getSelectedMeeting().getFrom());
        to.setValue(meetingListService.getSelectedMeeting().getTo());
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObjectProperty<LocalDate> fromProperty() {
        return from;
    }

    public ObjectProperty<LocalDate> toProperty() {
        return to;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty distanceProperty() {
        return distance;
    }

    public StringProperty timeProperty() {
        return time;
    }

    public StringProperty informationProperty() {
        return information;
    }

    public BooleanProperty favoriteProperty() {
        return favorite;
    }

    private List<InvalidMeetingInput> manageTour(boolean isNewTour) {
        MeetingUpdateStatus status = MeetingUpdateStatus.SUCCESS;
        List<InvalidMeetingInput> invalidMeetingInput = new ArrayList<>();

        Map<StringProperty, InvalidMeetingInput> inputs = Map.of(
                name, InvalidMeetingInput.INVALID_TITLE
                //from, InvalidMeetingInput.INVALID_FROM,
                //to, InvalidMeetingInput.INVALID_TO
        );
        inputs.forEach((key, error) -> {
            if (key.getValue() == null || key.getValue().trim().isEmpty()) {
                invalidMeetingInput.add(error);
            }
        });

        if (invalidMeetingInput.isEmpty()) {
            if (isNewTour) {
                //meetingListService.addMeeting(new Meeting(name.get(), description.get(), from.get(), to.get(),
                //        type.get(), distance.get(), time.get(), information.get(), favorite.get())); todo
            } else {
                //meetingListService.updateMeeting(name.get(), description.get(), from.get(), to.get(),
                //        type.get(), distance.get(), time.get(), information.get(), favorite.get());
            }
            eventManager.publish(isNewTour ? Event.MEETING_CREATED : Event.MEETING_UPDATED, status.getValue());
            log.info(isNewTour ? "Crating tour succeeded" : "Updating tour succeeded");
        } else {
            status = MeetingUpdateStatus.FAILURE;
            eventManager.publish(isNewTour ? Event.MEETING_CREATED_FAILED : Event.MEETING_UPDATED_FAILED, status.getValue());
            log.warn(isNewTour ? "Crating tour failed" : "Updating tour failed");
        }

        return invalidMeetingInput;
    }

    public List<InvalidMeetingInput> createTour() {
        return manageTour(true);
    }

    public List<InvalidMeetingInput> updateTour() {
        return manageTour(false);
    }
}