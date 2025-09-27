package org.example.meetingplanner.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeetingTest {

    @Test
    void testConstructorSetsFields() {
        LocalDateTime now = LocalDateTime.now();
        Meeting meeting = new Meeting("Title", now.plusHours(1), now.plusHours(2), "Agenda");
        assertEquals("Title", meeting.getTitle());
        assertEquals(now.plusHours(1), meeting.getFrom());
        assertEquals(now.plusHours(2), meeting.getTo());
        assertEquals("Agenda", meeting.getAgenda());
    }

    @Test
    void testUpdateMeetingChangesFields() {
        LocalDateTime now = LocalDateTime.now();
        Meeting meeting = new Meeting("Title", now.plusHours(1), now.plusHours(2), "Agenda");
        meeting.updateMeeting("New Title", now.plusHours(3), now.plusHours(4), "New Agenda");
        assertEquals("New Title", meeting.getTitle());
        assertEquals(now.plusHours(3), meeting.getFrom());
        assertEquals(now.plusHours(4), meeting.getTo());
        assertEquals("New Agenda", meeting.getAgenda());
    }

    @Test
    void testAddRemoveLogs() {
        LocalDateTime now = LocalDateTime.now();
        Meeting meeting = new Meeting("Title", now.plusHours(1), now.plusHours(2), "Agenda");
        MeetingNote note = new MeetingNote("Note");
        meeting.updateNotes(List.of(note));
        assertTrue(meeting.getNotes().contains(note));
        meeting.removeFromNotes(note);
        assertFalse(meeting.getNotes().contains(note));
    }
}
