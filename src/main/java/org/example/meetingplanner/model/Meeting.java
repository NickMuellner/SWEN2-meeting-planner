package org.example.meetingplanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import org.example.meetingplanner.util.LocalDateDeserializer;
import org.example.meetingplanner.util.LocalDateSerializer;

import java.time.LocalDate;
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

    @Column(name = "from")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate from;

    @Column(name = "to")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate to;

    @Column(name = "agenda")
    private String agenda;

    public Meeting() {
    }
    
    public Meeting(
            String title,
            LocalDate from,
            LocalDate to,
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
            @JsonProperty("from") LocalDate from,
            @JsonProperty("to") LocalDate to,
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

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public String getAgenda() {
        return agenda == null ? "" : agenda;
    }

    public void updateMeeting(String title, LocalDate from, LocalDate to, String agenda) {
        this.title = title;
        this.from = from;
        this.to = to;
        this.agenda = agenda;
    }

    public void addToNotes(MeetingNote note) {
        this.notes.add(note);
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
