package org.example.meetingplanner.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.meetingplanner.event.Event;
import org.example.meetingplanner.event.EventManager;

public class MainViewModel {

    private final StringProperty searchText = new SimpleStringProperty("");


    private final BooleanProperty meetingManageViewVisible = new SimpleBooleanProperty();

    private final EventManager eventManager;

    public MainViewModel(EventManager eventManager) {
        this.eventManager = eventManager;
        eventManager.subscribe(Event.MEETING_SELECTED, this::onSelectedMeeting);
        eventManager.subscribe(Event.MEETING_DESELECTED, this::onDeselectedMeeting);
    }

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public void search() {
        eventManager.publish(Event.SEARCH_STARTED, searchText.getValue());
    }

    private void onSelectedMeeting(Object s) {
        meetingManageViewVisible.set(true);
    }

    private void onDeselectedMeeting(Object s) {
        meetingManageViewVisible.set(false);
    }

    public BooleanProperty meetingManageViewVisibleProperty() {
        return meetingManageViewVisible;
    }
}