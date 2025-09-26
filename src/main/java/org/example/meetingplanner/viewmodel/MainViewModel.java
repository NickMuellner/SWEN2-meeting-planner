package org.example.meetingplanner.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;

public class MainViewModel {

    private final StringProperty searchText = new SimpleStringProperty("");

    private final EventManager eventManager;

    public MainViewModel(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public void search() {
        eventManager.publish(Event.SEARCH_STARTED, searchText.getValue());
    }
}