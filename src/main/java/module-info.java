module org.example.meetingplanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires javafx.web;
    requires java.desktop;
    requires kernel;
    requires layout;
    requires io;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires jdk.jsobject;
    requires javafx.swing;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires javafx.base;


    opens org.example.meetingplanner.viewmodel to javafx.fxml;
    opens org.example.meetingplanner.view to javafx.fxml;
    opens org.example.meetingplanner to javafx.fxml;
    opens org.example.meetingplanner.model to com.fasterxml.jackson.databind, org.hibernate.orm.core;
    exports org.example.meetingplanner;
    exports org.example.meetingplanner.view;
    exports org.example.meetingplanner.model;
    exports org.example.meetingplanner.viewmodel;
    exports org.example.meetingplanner.service;
    exports org.example.meetingplanner.event;
    exports org.example.meetingplanner.repository;
    exports org.example.meetingplanner.util;
}