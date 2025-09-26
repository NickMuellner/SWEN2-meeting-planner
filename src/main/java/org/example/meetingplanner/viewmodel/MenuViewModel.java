package org.example.meetingplanner.viewmodel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.model.Meeting;
import org.example.meetingplanner.report.PdfGenerator;
import org.example.meetingplanner.service.MeetingListService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MenuViewModel {
    private static final Logger log = LogManager.getLogger(MenuViewModel.class);
    private final MeetingListService meetingListService;
    private final EventManager eventManager;

    private File selectedFile;

    public MenuViewModel(EventManager eventManager, MeetingListService meetingListService) {
        this.meetingListService = meetingListService;
        this.eventManager = eventManager;
    }

    public void exportFile(File selectedFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Configure mapper to fail on empty beans
        mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        List<Meeting> meetings = meetingListService.getMeetings();
        log.info("Exporting {} tours", meetings.size());
        
        // Initialize lazy collections to ensure they're loaded
        for (Meeting meeting : meetings) {
            // Force initialization of logs collection
            int logSize = meeting.getNotes().size();
            log.info("Tour: {} with {} logs", meeting.getTitle(), logSize);
        }
        
        String jsonString = mapper.writeValueAsString(meetings);
        log.info("JSON length: {}", jsonString.length());
        log.debug("JSON content: {}", jsonString);
        
        FileWriter writer = new FileWriter(selectedFile);
        writer.write(jsonString);
        writer.close();
    }

    public void importFile(File selectedFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Meeting> meetings = mapper.readValue(selectedFile, new TypeReference<>() {
        });
        meetingListService.getMeetings().addAll(meetings);
    }

    public void tourReport(File selectedFile) throws RuntimeException {
        if (meetingListService.getSelectedMeeting() == null) {
            throw new RuntimeException("No tour selected");
        }
        this.selectedFile = selectedFile;
    }

    public void meetingReport(File selectedFile) throws IOException {
        PdfGenerator.generateMeetingReport(selectedFile, meetingListService.getMeetings());
    }
}
