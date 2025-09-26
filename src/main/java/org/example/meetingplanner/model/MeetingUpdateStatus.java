package org.example.meetingplanner.model;

import javafx.scene.paint.Color;

public enum MeetingUpdateStatus {
    SUCCESS("Success", Color.GREEN),
    FAILURE("Failure", Color.RED),
    SEARCHED("Searched", Color.BLUE);

    private final String value;
    private final Color color;

    MeetingUpdateStatus(String status, Color color) {
        this.value = status;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }
}
