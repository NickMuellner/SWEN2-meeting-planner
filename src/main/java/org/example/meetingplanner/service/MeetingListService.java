package org.example.meetingplanner.service;

import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.repository.MeetingRepository;

import java.time.LocalDate;
import java.util.ArrayList;

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

    public void selectTour(Meeting selectedItem) {
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
        eventManager.publish(Event.MEETING_SELECTED, "");
    }

    public void addMeeting(Meeting meeting) {
        meetings.add(meeting);
        meetingRepository.save(meeting);
        selectedMeeting = meeting;
    }

    public void addMeetingNote(MeetingNote meetingNote) {
        meetingNote.setMeeting(selectedMeeting);
        selectedMeeting.addToNotes(meetingNote);
        meetingRepository.saveMeetingNote(meetingNote);
    }

    public void removeMeeting(Meeting meeting) {
        meetings.remove(meeting);
        meetingRepository.delete(meeting);
        deselectMeeting();
    }

    public void updateMeeting(String title, LocalDate from, LocalDate to, String agenda) {
        selectedMeeting.updateMeeting(title, from, to, agenda);
        meetingRepository.save(selectedMeeting);
    }

    public void removeMeetingNote(MeetingNote meetingNote) {
        selectedMeeting.removeFromNotes(meetingNote);
        meetingRepository.deleteMeetingNote(meetingNote);
    }

    public void updateMeetingNote(MeetingNote note) {
        meetingRepository.saveMeetingNote(note);
    }
}
