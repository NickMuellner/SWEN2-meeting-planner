package org.example.meetingplanner.view;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.meetingplanner.viewmodel.MeetingDetailViewModel;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MeetingDetailView implements Initializable {

    private static final Logger log = LogManager.getLogger(MeetingDetailView.class);
    private final MeetingDetailViewModel viewModel;

    @FXML
    private TextField popularity, childFriendliness;

    @FXML
    private TabPane tabPane;

    @FXML
    private WebView map;
    private WebEngine webEngine;

    public MeetingDetailView(MeetingDetailViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.trace("Initialize TourDetailView");

        tabPane.visibleProperty().bindBidirectional(viewModel.tabPaneVisibleProperty());
        map.visibleProperty().bindBidirectional(viewModel.tabPaneVisibleProperty());
        popularity.textProperty().bindBidirectional(viewModel.popularityProperty());
        childFriendliness.textProperty().bindBidirectional(viewModel.childFriendlinessProperty());

        DoubleProperty tourDistance = new SimpleDoubleProperty();
        DoubleProperty tourDuration = new SimpleDoubleProperty();
        tourDistance.bindBidirectional(viewModel.tourDistanceProperty());
        tourDuration.bindBidirectional(viewModel.tourDurationProperty());

        viewModel.locationProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                Platform.runLater(() -> webEngine.executeScript(newVal));
            }
        });

        viewModel.sendImageProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal) {
                try {
                    byte[] image = takeWebViewScreenshot();
                    viewModel.sendScreenshot(image);
                } catch (IOException e) {
                    log.error(e);
                }
            }
        });
    }

    @FXML
    private void calcProperties() {
        log.trace("Calculating tour properties");
        viewModel.calcProperties();
    }

    public byte[] takeWebViewScreenshot() throws IOException {
        WritableImage image = new WritableImage((int) map.getWidth(), (int) map.getHeight());
        map.snapshot(null, image);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputStream);
        return outputStream.toByteArray();
    }
}
