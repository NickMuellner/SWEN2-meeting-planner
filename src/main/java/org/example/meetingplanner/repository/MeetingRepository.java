package org.example.meetingplanner.repository;

import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;

import java.util.List;

public interface MeetingRepository {
    List<Meeting> findAll();

    void save(Meeting meeting);

    void delete(Meeting meeting);

    void saveMeetingNote(MeetingNote meetingNote);

    void deleteMeetingNote(MeetingNote meetingNote);
}
