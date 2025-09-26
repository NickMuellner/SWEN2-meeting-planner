package org.example.meetingplanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "meeting_note")
public class MeetingNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "note")
    private String note;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    @JsonBackReference
    private Meeting meeting;

    public MeetingNote() {
    }
    
    public MeetingNote(String note) {
        this.note = note;
    }

    @JsonCreator
    public MeetingNote(
            @JsonProperty("id") int id,
            @JsonProperty("note") String note
    ) {
        this(note);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note == null ? "" : note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    public Meeting getMeeting() {
        return meeting;
    }
    
    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    @Override
    public String toString() {
        return note;
    }
}
