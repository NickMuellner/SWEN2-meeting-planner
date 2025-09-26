package org.example.meetingplanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.event.EventManager;
import org.example.meetingplanner.repository.MeetingRepository;
import org.example.meetingplanner.repository.MeetingRepositoryJpaImpl;
import org.example.meetingplanner.service.MeetingListService;
import org.example.meetingplanner.view.MainView;
import org.example.meetingplanner.view.MeetingListView;
import org.example.meetingplanner.view.MeetingManageView;
import org.example.meetingplanner.view.MenuView;
import org.example.meetingplanner.viewmodel.MainViewModel;
import org.example.meetingplanner.viewmodel.MeetingListViewModel;
import org.example.meetingplanner.viewmodel.MeetingManageViewModel;
import org.example.meetingplanner.viewmodel.MenuViewModel;

public class ViewFactory {

    private static final Logger log = LogManager.getLogger(ViewFactory.class);
    private static ViewFactory instance;

    private final MeetingListService meetingListService;
    private final EventManager eventManager;

    private ViewFactory(MeetingListService meetingListService, EventManager eventManager) {
        this.meetingListService = meetingListService;
        this.eventManager = eventManager;
    }

    public static ViewFactory getInstance() {
        if (null == instance) {
            EventManager eventManager = new EventManager();
            MeetingRepository meetingRepository = new MeetingRepositoryJpaImpl();
            instance = new ViewFactory(new MeetingListService(eventManager, meetingRepository), eventManager);
        }

        return instance;
    }

    public Object create(Class<?> viewClass) {
        log.debug("Creating View: {}", viewClass.getName());

        if (MainView.class == viewClass) {
            return new MainView(new MainViewModel(eventManager));
        }

        if (MeetingListView.class == viewClass) {
            return new MeetingListView(new MeetingListViewModel(eventManager, meetingListService));
        }

        if (MenuView.class == viewClass) {
            return new MenuView(new MenuViewModel(eventManager, meetingListService));
        }

        if (MeetingManageView.class == viewClass) {
            return new MeetingManageView(new MeetingManageViewModel(eventManager, meetingListService));
        }

        throw new IllegalArgumentException(
                "Unknown view class: " + viewClass
        );
    }
}