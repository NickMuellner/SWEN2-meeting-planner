package org.example.meetingplanner.viewmodel;

import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.InvalidMeetingInput;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.repository.MeetingRepository;
import org.example.meetingplanner.service.MeetingListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeetingManageViewModelTest {

    private MeetingManageViewModel viewModel;
    private InMemoryRepo repo;
    private EventManager eventManager;
    private MeetingListService meetingListService;

    @BeforeEach
    void setUp() {
        eventManager = new EventManager();
        repo = new InMemoryRepo();
        meetingListService = new MeetingListService(eventManager, repo);
        viewModel = new MeetingManageViewModel(eventManager, meetingListService);
    }

    @Test
    void createMeetingMissingFields() {
        List<InvalidMeetingInput> invalidInputs = viewModel.createMeeting();

        assertTrue(invalidInputs.contains(InvalidMeetingInput.INVALID_TITLE));
        assertTrue(invalidInputs.contains(InvalidMeetingInput.INVALID_DATE));
        assertTrue(invalidInputs.contains(InvalidMeetingInput.INVALID_TIME));
        assertEquals(0, repo.savedMeetings.size());
    }

    @Test
    void createMeetingSuccessAddsMeeting() {
        LocalDateTime now = LocalDateTime.now();

        viewModel.titleProperty().set("Title");
        viewModel.fromDateProperty().set(now.toLocalDate());
        viewModel.toDateProperty().set(now.toLocalDate());
        viewModel.fromTimeProperty().set(now.toLocalTime());
        viewModel.toTimeProperty().set(now.toLocalTime().plusHours(1));
        viewModel.agendaProperty().set("Agenda");

        List<InvalidMeetingInput> invalid = viewModel.createMeeting();

        assertTrue(invalid.isEmpty());
        assertEquals(1, repo.savedMeetings.size());
        assertEquals("Title", repo.savedMeetings.getFirst().getTitle());
    }

    @Test
    void updateMeetingUpdatesSelectedMeeting() {
        LocalDateTime now = LocalDateTime.now();
        Meeting existing = new Meeting("Title", now.plusHours(1), now.plusHours(2), "Agenda");
        repo.savedMeetings.add(existing);
        meetingListService.selectMeeting(existing);

        viewModel.titleProperty().set("New Title");
        viewModel.fromDateProperty().set(now.toLocalDate());
        viewModel.toDateProperty().set(now.toLocalDate());
        viewModel.fromTimeProperty().set(now.toLocalTime().plusHours(3));
        viewModel.toTimeProperty().set(now.toLocalTime().plusHours(5));
        viewModel.agendaProperty().set("New Agenda");

        List<InvalidMeetingInput> invalid = viewModel.updateMeeting();

        assertTrue(invalid.isEmpty());
        assertEquals("New Title", existing.getTitle());
    }

    private static class InMemoryRepo implements MeetingRepository {
        final List<Meeting> savedMeetings = new ArrayList<>();

        @Override
        public List<Meeting> findAll() {
            return new ArrayList<>(savedMeetings);
        }

        @Override
        public void save(Meeting meeting) {
            if (!savedMeetings.contains(meeting)) {
                savedMeetings.add(meeting);
            }
        }

        @Override
        public void delete(Meeting meeting) {
            savedMeetings.remove(meeting);
        }

        @Override
        public void saveMeetingNote(MeetingNote meetingNote) {
            // no-op for test
        }

        @Override
        public void deleteMeetingNote(MeetingNote meetingNote) {
            // no-op for test
        }
    }
}
