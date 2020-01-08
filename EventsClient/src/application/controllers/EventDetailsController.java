package application.controllers;

import application.data.EventData;
import application.request.GetUserRequst;
import application.response.EventDataResponse;
import application.util.ClientUtil;
import application.util.EventDateUtil;
import application.util.SessionInfo;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class EventDetailsController implements Initializable {
    @FXML
    private Button btnBack;

    @FXML
    private Label lblEventLocation;
    @FXML
    private Label lblEventCode;
    @FXML
    private Label lblEventDay;
    @FXML
    private Label lblEventDescription;
    @FXML
    private Label lblEventYear;
    @FXML
    private Label lblEventMonth;
    @FXML
    private Label lblEventSeatsNumber;
    @FXML
    private Label lblEventOrganizer;
    @FXML
    private Label lblEventName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GetUserRequst getUserRequst = new GetUserRequst();
        getUserRequst.setEmail(SessionInfo.getCurrentUserEmail());
        Gson gson = new Gson();
        try {
            String serverResponse = ClientUtil.communicateWithServer("getEventByInvitationId", SessionInfo.getSelectedInvitation());
            EventDataResponse response = gson.fromJson(serverResponse, EventDataResponse.class);
            if ("200".equalsIgnoreCase(response.getStatusCode())) {
                EventData eventData = response.getEvents().get(0);
                lblEventLocation.setText(eventData.getLocation());
                lblEventName.setText(eventData.getName());
                lblEventSeatsNumber.setText(String.valueOf(eventData.getNrSeats()));
                lblEventDescription.setText(eventData.getDescription());
                lblEventCode.setText("Ref: " + eventData.getEventId());
                lblEventOrganizer.setText(eventData.getOrganizer());
                Date eventDate = eventData.getDate();
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                cal.setTime(eventDate);
                lblEventDay.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                lblEventYear.setText(String.valueOf(cal.get(Calendar.YEAR)));
                lblEventMonth.setText(EventDateUtil.getEventMonth(String.valueOf(cal.get(Calendar.MONTH))));
            }
        } catch (Exception e) {
            // TO DO
        }
    }

    @FXML
    public void handleBackButtonAction(MouseEvent event) {
        if (event.getSource() == btnBack) {
            SessionInfo.selectedInvitation = "NONE";
            openInvitationView(event);
        }
    }

    private void openInvitationView(MouseEvent event) {
        SessionInfo.selectedInvitation = "NONE";
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            // stage.setMaximized(true);
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/InvitationView.fxml")));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
