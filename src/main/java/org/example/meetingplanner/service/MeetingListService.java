package org.example.meetingplanner.service;

import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.repository.MeetingRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MeetingListService {
    private final EventManager eventManager;
    private final ArrayList<Meeting> meetings;
    private final MeetingRepository meetingRepository;
    private Meeting selectedMeeting;

    public MeetingListService(EventManager eventManager, MeetingRepository meetingRepository) {
        this.eventManager = eventManager;
        this.meetingRepository = meetingRepository;
        meetings = new ArrayList<>(meetingRepository.findAll());
    }

    public void selectMeeting(Meeting selectedItem) {
        selectedMeeting = selectedItem;
        eventManager.publish(Event.MEETING_SELECTED, "");
    }

    public ArrayList<Meeting> getMeetings() {
        return meetings;
    }

    public Meeting getSelectedMeeting() {
        return selectedMeeting;
    }

    public void deselectMeeting() {
        selectedMeeting = null;
        eventManager.publish(Event.MEETING_DESELECTED, "");
    }

    public void addMeeting(Meeting meeting) {
        meetings.add(meeting);
        meetingRepository.save(meeting);
        selectedMeeting = meeting;
    }

    public void updateMeeting(String title, LocalDateTime from, LocalDateTime to, String agenda) {
        selectedMeeting.updateMeeting(title, from, to, agenda);
        meetingRepository.save(selectedMeeting);
    }

    public void removeMeeting(Meeting meeting) {
        meetings.remove(meeting);
        meetingRepository.delete(meeting);
        deselectMeeting();
    }

    public void updateMeetingNotes(List<MeetingNote> meetingNotes) {
        for (MeetingNote meetingNote : meetingNotes) {
            meetingNote.setMeeting(selectedMeeting);
            meetingRepository.saveMeetingNote(meetingNote);
        }
        selectedMeeting.updateNotes(meetingNotes);
    }

    public void removeMeetingNote(MeetingNote meetingNote) {
        selectedMeeting.removeFromNotes(meetingNote);
        meetingRepository.deleteMeetingNote(meetingNote);
    }
}
