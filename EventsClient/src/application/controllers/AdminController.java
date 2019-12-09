package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

import application.data.EventData;
import application.data.UserData;
import application.request.GetUserRequst;
import application.response.EventDataResponse;
import application.response.UserDataResponse;
import application.util.ClientUtil;
import application.util.EventDateUtil;
import application.util.SessionInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminController implements Initializable {

	@FXML
	private Label lblUserName;

	@FXML
	private Label lblUserRole;

	@FXML
	private Button btnCreateEvent;

	@FXML
	private VBox pnl_scroll;

	@FXML
	public void handleButtonAction(MouseEvent event) {

	}

	@FXML
	public void handleCreateEventButtonAction(MouseEvent event) {
		if (event.getSource() == btnCreateEvent) {
			try {
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();
				stage.setMaximized(false);
				stage.close();
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/CreateEventView.fxml")));
				stage.setScene(scene);
				stage.show();
			} catch (IOException e) {
				// TO DO
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		GetUserRequst getUserRequst = new GetUserRequst();
		getUserRequst.setEmail(SessionInfo.getCurrentUserEmail());
		Gson gson = new Gson();
		try {
			String request = gson.toJson(getUserRequst);
			String serverResponse = ClientUtil.communicateWithServer("getUser", request);
			UserDataResponse response = gson.fromJson(serverResponse, UserDataResponse.class);
			if ("200".equalsIgnoreCase(response.getStatusCode())) {
				UserData userData = response.getUserData();
				lblUserName.setText(userData.getFirstName() + " " + userData.getLastName());
				lblUserRole.setText(userData.getRoles().get(0).getRoleName());
				try {
					String serverEventResponse = ClientUtil.communicateWithServer("getAllEvents", "");
					EventDataResponse eventsResponse = gson.fromJson(serverEventResponse, EventDataResponse.class);
					if ("200".equalsIgnoreCase(response.getStatusCode())) {
						refreshNodes(eventsResponse.getEvents());
					}
				} catch (Exception e) {
					// TO DO
				}
				refreshNodes(userData.getEvents());
			}
		} catch (Exception e) {
			// TO DO
		}
	}

	private void refreshNodes(List<EventData> eventDataList) {
		pnl_scroll.getChildren().clear();

		Node[] nodes = new Node[eventDataList.size()];
		for (int i = 0; i < eventDataList.size(); i++) {
			try {
				nodes[i] = (Node) FXMLLoader.load(getClass().getResource("../ui/Event.fxml"));
				((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(2))
						.setText(eventDataList.get(i).getName());
				((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(5))
						.setText(eventDataList.get(i).getOrganizer());
                ((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(6))
                        .setText(String.valueOf(eventDataList.get(i).getNrSeats()));
				((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(15))
						.setText(eventDataList.get(i).getLocation());
				((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(14))
						.setText("Ref: " + ThreadLocalRandom.current().nextInt());
				Date eventDate = eventDataList.get(i).getDate();
				((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(11))
						.setText(String.valueOf(eventDate.getDay()));
				((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(12))
						.setText(EventDateUtil.getEventMonth(eventDate));
				((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(13))
						.setText(String.valueOf(eventDate.getYear()));
				((Label) ((Pane) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(8))
						.getChildren().get(0)).setText(eventDataList.get(i).getDescription());

				pnl_scroll.getChildren().add(nodes[i]);

			} catch (IOException ex) {
				// TO DO
			}
		}
	}
}
