package org.example.meetingplanner.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MeetingTest {

    @Test
    void testConstructorSetsFields() {
        Meeting meeting = new Meeting("Name", "Desc", "From", "To", "Car", "10km", "30m", "Info", false);
        assertEquals("Name", meeting.getTitle());
        assertEquals("Desc", meeting.getAgenda());
        assertEquals("From", meeting.getFrom());
        assertEquals("To", meeting.getTo());
        assertEquals("Car", meeting.getTransportType());
        assertEquals("10km", meeting.getTourDistance());
        assertEquals("30m", meeting.getEstimatedTime());
        assertEquals("Info", meeting.getRouteInformation());
    }

    @Test
    void testUpdateTourChangesFields() {
        Meeting meeting = new Meeting("Old", "D", "A", "B", "Walk", "1km", "5m", "R", true);
        meeting.updateTour("New", "Desc", "X", "Y", "Bike", "2km", "10m", "Info", false);
        assertEquals("New", meeting.getTitle());
        assertEquals("Desc", meeting.getAgenda());
        assertEquals("X", meeting.getFrom());
        assertEquals("Y", meeting.getTo());
        assertEquals("Bike", meeting.getTransportType());
        assertEquals("2km", meeting.getTourDistance());
        assertEquals("10m", meeting.getEstimatedTime());
        assertEquals("Info", meeting.getRouteInformation());
    }

    @Test
    void testAddRemoveLogs() {
        Meeting meeting = new Meeting("n", "d", "f", "t", "b", "1", "2", "i", true);
        MeetingNote log = new MeetingNote(LocalDate.now(), "c", 1, 2, 3, 4);
        meeting.addToNotes(log);
        assertTrue(meeting.getNotes().contains(log));
        meeting.removeFromNotes(log);
        assertFalse(meeting.getNotes().contains(log));
    }
}
