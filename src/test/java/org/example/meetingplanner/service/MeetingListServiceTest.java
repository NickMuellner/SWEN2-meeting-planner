package org.example.meetingplanner.service;

import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.repository.MeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeetingListServiceTest {

    private InMemoryRepository repo;
    private EventManager eventManager;
    private MeetingListService service;

    @BeforeEach
    void setup() {
        repo = new InMemoryRepository();
        repo.meetings.add(new Meeting("one", "d", "f", "t", "car", "1", "2", "i", false));
        eventManager = new EventManager();
        service = new MeetingListService(eventManager, repo);
    }

    @Test
    void testSelectAndDeselect() {
        Meeting meeting = service.getMeetings().get(0);
        service.selectTour(meeting);
        assertEquals(meeting, service.getSelectedMeeting());
        service.deselectMeeting();
        assertNull(service.getSelectedMeeting());
    }

    @Test
    void testAddAndRemoveMeeting() {
        Meeting meeting = new Meeting("two", "d", "x", "y", "bike", "3", "4", "inf", false);
        service.addMeeting(meeting);
        assertTrue(service.getMeetings().contains(meeting));
        assertTrue(repo.meetings.contains(meeting));

        service.removeMeeting(meeting);
        assertFalse(service.getMeetings().contains(meeting));
        assertFalse(repo.meetings.contains(meeting));
    }

    @Test
    void testAddMeetingNoteAndUpdate() {
        Meeting meeting = service.getMeetings().get(0);
        service.selectTour(meeting);
        MeetingNote log = new MeetingNote(LocalDate.now(), "c", 1, 2, 3, 4);
        service.addMeetingNote(log);
        assertTrue(meeting.getNotes().contains(log));
        assertTrue(repo.logs.contains(log));

        service.updateMeeting("n", "d", "f", "t", "bus", "5", "6", "i", false);
        assertEquals("n", meeting.getTitle());
        assertEquals("bus", meeting.getTransportType());
    }

    private static class InMemoryRepository implements MeetingRepository {
        List<Meeting> meetings = new ArrayList<>();
        List<MeetingNote> logs = new ArrayList<>();

        @Override
        public List<Meeting> findAll() {
            return meetings;
        }

        @Override
        public Meeting findById(int id) {
            return meetings.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
        }

        @Override
        public void save(Meeting meeting) {
            if (!meetings.contains(meeting)) {
                meetings.add(meeting);
            }
        }

        @Override
        public void delete(Meeting meeting) {
            meetings.remove(meeting);
        }

        @Override
        public void saveMeetingNote(MeetingNote meetingNote) {
            logs.add(meetingNote);
        }

        @Override
        public void deleteMeetingNote(MeetingNote meetingNote) {
            logs.remove(meetingNote);
        }
    }
}
