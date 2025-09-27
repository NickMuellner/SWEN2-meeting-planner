package org.example.meetingplanner.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventManagerAdditionalTest {

    private EventManager manager;
    private TestListener listener1;
    private TestListener listener2;

    @BeforeEach
    void setup() {
        manager = new EventManager();
        listener1 = new TestListener();
        listener2 = new TestListener();
    }

    @Test
    void testNoNotificationForUnsubscribedEvent() {
        manager.subscribe(Event.MEETING_CREATED, listener1);
        manager.publish(Event.MEETING_UPDATED, "msg");
        assertTrue(listener1.messages.isEmpty());
    }

    @Test
    void testListenerReceivesMultipleEvents() {
        manager.subscribe(Event.MEETING_CREATED, listener1);
        manager.subscribe(Event.MEETING_UPDATED, listener1);
        manager.publish(Event.MEETING_CREATED, "A");
        manager.publish(Event.MEETING_UPDATED, "B");
        assertEquals(List.of("A", "B"), listener1.messages);
    }

    @Test
    void testDifferentListenersDifferentEvents() {
        manager.subscribe(Event.MEETING_CREATED, listener1);
        manager.subscribe(Event.MEETING_UPDATED, listener2);
        manager.publish(Event.MEETING_CREATED, "A");
        manager.publish(Event.MEETING_UPDATED, "B");
        assertEquals(List.of("A"), listener1.messages);
        assertEquals(List.of("B"), listener2.messages);
    }

    private static class TestListener implements EventListener {
        List<String> messages = new ArrayList<>();

        @Override
        public void event(Object message) {
            messages.add(message.toString());
        }
    }
}
