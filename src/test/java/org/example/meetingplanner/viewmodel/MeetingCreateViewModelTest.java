package org.example.meetingplanner.viewmodel;

import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.InvalidMeetingInput;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.model.MeetingNote;
import org.example.meetingplanner.repository.MeetingRepository;
import org.example.meetingplanner.service.MeetingListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeetingCreateViewModelTest {

    private MeetingCreateViewModel viewModel;
    private InMemoryRepo repo;
    private EventManager eventManager;
    private MeetingListService meetingListService;

    @BeforeEach
    void setUp() {
        eventManager = new EventManager();
        repo = new InMemoryRepo();
        meetingListService = new MeetingListService(eventManager, repo);
        viewModel = new MeetingCreateViewModel(eventManager, meetingListService);
    }

    @Test
    void createTourMissingFields() {
        List<InvalidMeetingInput> invalidInputs = viewModel.createTour();

        assertTrue(invalidInputs.contains(InvalidMeetingInput.INVALID_NAME));
        assertTrue(invalidInputs.contains(InvalidMeetingInput.INVALID_FROM));
        assertTrue(invalidInputs.contains(InvalidMeetingInput.INVALID_TO));
        assertEquals(0, repo.savedMeetings.size());
    }

    @Test
    void createTourSuccessAddsTour() {
        viewModel.nameProperty().set("Test");
        viewModel.descriptionProperty().set("desc");
        viewModel.fromProperty().set("a");
        viewModel.toProperty().set("b");
        viewModel.typeProperty().set("WALKING");
        viewModel.distanceProperty().set("1");
        viewModel.timeProperty().set("1h");
        viewModel.informationProperty().set("info");

        List<InvalidMeetingInput> invalid = viewModel.createTour();

        assertTrue(invalid.isEmpty());
        assertEquals(1, repo.savedMeetings.size());
        assertEquals("Test", repo.savedMeetings.get(0).getTitle());
    }

    @Test
    void updateTourUpdatesSelectedTour() {
        Meeting existing = new Meeting("Old", "old", "x", "y", "WALKING", "10", "1h", "info", false);
        repo.savedMeetings.add(existing);
        meetingListService.selectTour(existing);

        viewModel.nameProperty().set("NewName");
        viewModel.descriptionProperty().set("Desc");
        viewModel.fromProperty().set("X");
        viewModel.toProperty().set("Y");
        viewModel.typeProperty().set("CAR");
        viewModel.distanceProperty().set("20");
        viewModel.timeProperty().set("2h");
        viewModel.informationProperty().set("new info");

        List<InvalidMeetingInput> invalid = viewModel.updateTour();

        assertTrue(invalid.isEmpty());
        assertEquals("NewName", existing.getTitle());
    }

    private static class InMemoryRepo implements MeetingRepository {
        final List<Meeting> savedMeetings = new ArrayList<>();

        @Override
        public List<Meeting> findAll() {
            return new ArrayList<>(savedMeetings);
        }

        @Override
        public Meeting findById(int id) {
            return savedMeetings.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
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
