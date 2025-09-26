package org.example.meetingplanner.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private static final Logger log = LogManager.getLogger(EventManager.class);
    Map<Event, List<EventListener>> eventListeners;

    public EventManager() {
        this.eventListeners = new HashMap<>();
    }

    public void subscribe(Event event, EventListener listener) {
        log.trace("Class {} subscribing to {}", listener.getClass().getName().split("\\$\\$")[0], event.name());
        List<EventListener> listeners = eventListeners.getOrDefault(event, new ArrayList<>());
        listeners.add(listener);
        eventListeners.put(event, listeners);
    }

    public void publish(Event event, Object message) {
        log.debug("Publishing event {} with message \"{}\"", event.name(), message);
        List<EventListener> listeners = eventListeners.getOrDefault(event, new ArrayList<>());
        listeners.forEach(listener -> listener.event(message));
    }
}
