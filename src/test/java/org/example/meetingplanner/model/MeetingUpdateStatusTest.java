package org.example.meetingplanner.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MeetingUpdateStatusTest {
    @Test
    void testEnumValueAndColor() {
        assertEquals("Success", MeetingUpdateStatus.SUCCESS.getValue());
        assertNotNull(MeetingUpdateStatus.SUCCESS.getColor());
    }
}
