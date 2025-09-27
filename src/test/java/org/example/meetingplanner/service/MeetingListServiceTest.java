package org.example.meetingplanner.service;

import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.repository.MeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        LocalDateTime now = LocalDateTime.now();
        repo.meetings.add(new Meeting("Title", now.plusHours(1), now.plusHours(2), "Agenda"));
        eventManager = new EventManager();
        service = new MeetingListService(eventManager, repo);
    }

    @Test
    void testSelectAndDeselect() {
        Meeting meeting = service.getMeetings().getFirst();
        service.selectMeeting(meeting);
        assertEquals(meeting, service.getSelectedMeeting());
        service.deselectMeeting();
        assertNull(service.getSelectedMeeting());
    }

    @Test
    void testAddAndRemoveMeeting() {
        LocalDateTime now = LocalDateTime.now();
        Meeting meeting = new Meeting("Title", now.plusHours(1), now.plusHours(2), "Agenda");
        service.addMeeting(meeting);
        assertTrue(service.getMeetings().contains(meeting));
        assertTrue(repo.meetings.contains(meeting));

        service.removeMeeting(meeting);
        assertFalse(service.getMeetings().contains(meeting));
        assertFalse(repo.meetings.contains(meeting));
    }

    @Test
    void testAddMeetingNoteAndUpdate() {
        Meeting meeting = service.getMeetings().getFirst();
        service.selectMeeting(meeting);
        MeetingNote note = new MeetingNote("Note");
        service.updateMeetingNotes(List.of(note));
        assertTrue(meeting.getNotes().contains(note));
        assertTrue(repo.logs.contains(note));

        LocalDateTime now = LocalDateTime.now();
        service.updateMeeting("New Title", now.plusHours(5), now.plusHours(6), "Agenda");
        assertEquals("New Title", meeting.getTitle());
        assertEquals(now.plusHours(6), meeting.getTo());
    }

    private static class InMemoryRepository implements MeetingRepository {
        List<Meeting> meetings = new ArrayList<>();
        List<MeetingNote> logs = new ArrayList<>();

        @Override
        public List<Meeting> findAll() {
            return meetings;
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
