package org.example.meetingplanner.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeetingLogTest {

    @Test
    void testConstructorAndGetters() {
        LocalDate date = LocalDate.of(2024, 1, 1);
        MeetingNote log = new MeetingNote(date, "Nice tour", 2, 10.5, 120.0, 4);

        assertEquals(date, log.getDate());
        assertEquals("Nice tour", log.getComment());
        assertEquals(2, log.getDifficulty());
        assertEquals(10.5, log.getDistance());
        assertEquals(120.0, log.getDuration());
        assertEquals(4, log.getRating());
    }

    @Test
    void testNullCommentReturnsEmptyString() {
        MeetingNote log = new MeetingNote(LocalDate.now(), null, 1, 1.0, 1.0, 1);
        assertEquals("", log.getComment());
    }

    @Test
    void testSettersUpdateValues() {
        MeetingNote log = new MeetingNote(LocalDate.now(), "initial", 1, 1.0, 1.0, 1);

        LocalDate newDate = LocalDate.of(2024, 2, 2);
        log.setDate(newDate);
        log.setComment("changed");
        log.setDifficulty(3);
        log.setDistance(5.5);
        log.setDuration(200.0);
        log.setRating(5);

        assertEquals(newDate, log.getDate());
        assertEquals("changed", log.getComment());
        assertEquals(3, log.getDifficulty());
        assertEquals(5.5, log.getDistance());
        assertEquals(200.0, log.getDuration());
        assertEquals(5, log.getRating());
    }
}
