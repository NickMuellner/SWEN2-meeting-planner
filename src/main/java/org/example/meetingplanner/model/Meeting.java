package org.example.meetingplanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import org.example.meetingplanner.util.LocalDateTimeDeserializer;
import org.example.meetingplanner.util.LocalDateTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meeting")
public class Meeting {

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private final List<MeetingNote> notes = new ArrayList<>();
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "from_datetime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime from;

    @Column(name = "to_datetime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime to;

    @Column(name = "agenda")
    private String agenda;

    public Meeting() {
    }
    
    public Meeting(
            String title,
            LocalDateTime from,
            LocalDateTime to,
            String agenda
    ) {
        this.title = title;
        this.from = from;
        this.to = to;
        this.agenda = agenda;
    }

    @JsonCreator
    public Meeting(
            @JsonProperty("notes") List<MeetingNote> notes,
            @JsonProperty("id") int id,
            @JsonProperty("title") String title,
            @JsonProperty("from_datetime") LocalDateTime from,
            @JsonProperty("to_datetime") LocalDateTime to,
            @JsonProperty("agenda") String agenda
    ) {
        this(title, from, to, agenda);
        this.notes.addAll(notes);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public String getAgenda() {
        return agenda == null ? "" : agenda;
    }

    public void updateMeeting(String title, LocalDateTime from, LocalDateTime to, String agenda) {
        this.title = title;
        this.from = from;
        this.to = to;
        this.agenda = agenda;
    }

    public void updateNotes(List<MeetingNote> notes) {
        this.notes.clear();
        this.notes.addAll(notes);
    }

    public void removeFromNotes(MeetingNote note) {
        this.notes.remove(note);
    }

    public List<MeetingNote> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return title;
    }
}
