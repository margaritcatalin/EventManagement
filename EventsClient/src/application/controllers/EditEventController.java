package application.controllers;

import application.data.EventData;
import application.request.EditEventRequest;
import application.request.GetUserRequst;
import application.response.EventDataResponse;
import application.response.SimpleResponse;
import application.util.ClientUtil;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class EditEventController implements Initializable {
    @FXML
    private Button btnBack;

    @FXML
    private Button btnSave;

    @FXML
    private TextField txtLocation;

    @FXML
    private TextField txtEventName;

    @FXML
    private TextField txtNumberOfSeats;

    @FXML
    private TextField txtDescription;

    @FXML
    private DatePicker txtEventDate;

    @FXML
    private Label serverLblError;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GetUserRequst getUserRequst = new GetUserRequst();
        getUserRequst.setEmail(SessionInfo.getCurrentUserEmail());
        Gson gson = new Gson();
        try {
            String request = gson.toJson(getUserRequst);
            String serverResponse = ClientUtil.communicateWithServer("getEventById", SessionInfo.getSelectedElementCode());
            EventDataResponse response = gson.fromJson(serverResponse, EventDataResponse.class);
            if ("200".equalsIgnoreCase(response.getStatusCode())) {
                EventData eventData = response.getEvents().get(0);
                txtLocation.setText(eventData.getLocation());
                txtEventName.setText(eventData.getName());
                txtNumberOfSeats.setText(String.valueOf(eventData.getNrSeats()));
                txtDescription.setText(eventData.getDescription());
                ZoneId defaultZoneId = ZoneId.systemDefault();
                Instant instant = eventData.getDate().toInstant();
                txtEventDate.setValue(instant.atZone(defaultZoneId).toLocalDate());
            }
        } catch (Exception e) {
            // TO DO
        }
    }

    @FXML
    public void handleCreateEventButtonAction(MouseEvent event) {

        if (event.getSource() == btnSave && "Success".equalsIgnoreCase(editEvent(event))) {
            SessionInfo.selectedElementCode = "NONE";
            openAdminView(event);
        }
    }

    @FXML
    public void handleBackButtonAction(MouseEvent event) {
        if (event.getSource() == btnBack) {
            SessionInfo.selectedElementCode = "NONE";
            openAdminView(event);
        }
    }

    private void openAdminView(MouseEvent event) {
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            // stage.setMaximized(true);
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/AdminHomeView.fxml")));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private String editEvent(MouseEvent event) {
        String status = "Success";
        Gson gson = new Gson();
        EditEventRequest editEventRequest = new EditEventRequest();

        LocalDate localDate = txtEventDate.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);
        editEventRequest.setDate(date);
        editEventRequest.setEventId(SessionInfo.getSelectedElementCode());
        editEventRequest.setDescription(txtDescription.getText());
        editEventRequest.setLocation(txtLocation.getText());
        editEventRequest.setName(txtEventName.getText());
        editEventRequest.setNrSeats(Integer.valueOf(txtNumberOfSeats.getText()));
        try {
            String request = gson.toJson(editEventRequest);
            String serverResponse = ClientUtil.communicateWithServer("editEvent", request);
            SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
            if ("500".equalsIgnoreCase(response.getStatusCode())) {
                setServerLblError(Color.TOMATO, response.getMessage());
                status = "Error";
            }
        } catch (Exception e) {
            status = "Error";
            setServerLblError(Color.TOMATO, "Internal Server Error");
        }

        return status;
    }

    private void setServerLblError(Color color, String text) {
        serverLblError.setTextFill(color);
        serverLblError.setText(text);
    }

}
