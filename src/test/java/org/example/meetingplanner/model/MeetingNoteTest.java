package org.example.meetingplanner.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeetingNoteTest {

    @Test
    void testConstructorAndGetters() {
        MeetingNote note = new MeetingNote("New Meeting Note");
        assertEquals("New Meeting Note", note.getNote());
    }

    @Test
    void testNullCommentReturnsEmptyString() {
        MeetingNote note = new MeetingNote(null);
        assertEquals("", note.getNote());
    }

    @Test
    void testSettersUpdateValues() {
        MeetingNote note = new MeetingNote("New Meeting Note");
        note.setNote("Another Meeting Note");
        assertEquals("Another Meeting Note", note.getNote());
    }
}
