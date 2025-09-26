package org.example.meetingplanner.view;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.viewmodel.MainViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class MainView implements Initializable {

    private static final Logger log = LogManager.getLogger(MainView.class);
    private final MainViewModel viewModel;

    @FXML
    private TextField searchInput;

    @FXML
    private Node meetingDetails;

    public MainView(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize MainView");

        searchInput.textProperty().bindBidirectional(viewModel.searchTextProperty());
        meetingDetails.visibleProperty().bindBidirectional(viewModel.meetingManageViewVisibleProperty());
    }

    @FXML
    public void onSearch() {
        log.trace("Searching \"{}\"", searchInput.getText());
        viewModel.search();
    }

}
