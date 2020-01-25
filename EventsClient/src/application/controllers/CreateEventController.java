package application.controllers;

import application.request.CreateEventRequest;
import application.request.RegisterRequest;
import application.response.SimpleResponse;
import application.util.ClientUtil;
import application.util.SessionInfo;
import application.util.ValidationUtil;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class CreateEventController implements Initializable {

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

	@FXML
	public void handleCreateEventButtonAction(MouseEvent event) {

		if (event.getSource() == btnSave && "Success".equalsIgnoreCase(createEvent(event))) {
			openAdminView(event);
		}
	}

	@FXML
	public void handleBackButtonAction(MouseEvent event) {
		if (event.getSource() == btnBack) {
			openAdminView(event);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	private String createEvent(MouseEvent event) {
		String status = "Success";
		Gson gson = new Gson();
		CreateEventRequest createEventRequest = new CreateEventRequest();

		LocalDate localDate = txtEventDate.getValue();
		Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
		Date date = Date.from(instant);
		createEventRequest.setDate(date);

		createEventRequest.setDescription(txtDescription.getText());
		createEventRequest.setLocation(txtLocation.getText());
		createEventRequest.setName(txtEventName.getText());
		createEventRequest.setNrSeats(Integer.valueOf(txtNumberOfSeats.getText()));
		createEventRequest.setOrganizer(SessionInfo.getCurrentUserEmail());
		try {
			String request = gson.toJson(createEventRequest);
			String serverResponse = ClientUtil.communicateWithServer("createEvent", request);
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

	private void setServerLblError(Color color, String text) {
		serverLblError.setTextFill(color);
		serverLblError.setText(text);
	}
}
