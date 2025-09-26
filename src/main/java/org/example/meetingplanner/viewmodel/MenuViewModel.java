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

    public MenuViewModel(EventManager eventManager, MeetingListService meetingListService) {
        this.meetingListService = meetingListService;
        this.eventManager = eventManager;
    }

    public void meetingReport(File selectedFile) throws IOException {
        if(meetingListService.getSelectedMeeting() == null) {
            throw new RuntimeException("No meeting selected");
        }
        PdfGenerator.generateMeetingReport(selectedFile, meetingListService.getSelectedMeeting());
    }
}
