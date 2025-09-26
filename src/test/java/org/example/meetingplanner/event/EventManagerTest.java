package org.example.meetingplanner.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EventManagerTest {

    private EventManager eventManager;
    private TestEventListener listener;

    @BeforeEach
    void setUp() {
        eventManager = new EventManager();
        listener = new TestEventListener();
    }

    @Test
    void testSingleSubscriberReceivesMessage() {
        eventManager.subscribe(Event.TOUR_SELECTED, listener);
        eventManager.publish(Event.TOUR_SELECTED, "Hello World");

        List<String> messages = listener.getMessages();
        assertEquals(1, messages.size(), "Listener should have received one message");
        assertEquals("Hello World", messages.get(0), "The received message should match the published message");
    }

    @Test
    void testMultipleSubscribersReceiveMessage() {
        TestEventListener secondListener = new TestEventListener();

        eventManager.subscribe(Event.TOUR_SELECTED, listener);
        eventManager.subscribe(Event.TOUR_SELECTED, secondListener);

        eventManager.publish(Event.TOUR_SELECTED, "Multi Listener Message");

        assertEquals(1, listener.getMessages().size(), "First listener should have received one message");
        assertEquals("Multi Listener Message", listener.getMessages().get(0));

        assertEquals(1, secondListener.getMessages().size(), "Second listener should have received one message");
        assertEquals("Multi Listener Message", secondListener.getMessages().get(0));
    }

    @Test
    void testPublishWithNoSubscribers() {
        assertDoesNotThrow(() -> eventManager.publish(Event.TOUR_SELECTED, "No Subscribers"));
    }

    @Test
    void testDuplicateSubscription() {
        eventManager.subscribe(Event.TOUR_SELECTED, listener);
        eventManager.subscribe(Event.TOUR_SELECTED, listener);

        eventManager.publish(Event.TOUR_SELECTED, "Duplicate Message");

        List<String> messages = listener.getMessages();
        assertEquals(2, messages.size(), "Listener should have received two messages");
        assertEquals("Duplicate Message", messages.get(0));
        assertEquals("Duplicate Message", messages.get(1));
    }

    private static class TestEventListener implements EventListener {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void event(Object message) {
            messages.add(message.toString());
        }

        public List<String> getMessages() {
            return messages;
        }
    }
}