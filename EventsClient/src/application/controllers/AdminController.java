package application.controllers;

import application.data.EventData;
import application.data.UserData;
import application.request.GetUserRequst;
import application.response.EventDataResponse;
import application.response.UserDataResponse;
import application.util.AvailableSwitchButton;
import application.util.ClientUtil;
import application.util.EventDateUtil;
import application.util.SessionInfo;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AdminController implements Initializable {

    @FXML
    private Label lblUserName;

    @FXML
    private Label lblUserRole;
    @FXML
    private Label lblUsersSection;
    @FXML
    private Button btnCreateEvent;

    @FXML
    private Button btnLogoutEvent;

    @FXML
    private Button btnEditProfileEvent;

    @FXML
    private AvailableSwitchButton lblSwtichAvailability;

    @FXML
    private VBox pnl_scroll;

    @FXML
    private Button btnNotificationEvent;

    @FXML
    private Button btnInvitationEvent;

    @FXML
    private Button btnOrganizersEvent;

    @FXML
    private Button btnUsersEvent;

    @FXML
    private Button btnCustomersEvent;

    @FXML
    private Label lblAllEvents;

    @FXML
    private Label lblNextEvents;

    @FXML
    private Label lblCompletedEvents;

    @FXML
    public void handleButtonAction(MouseEvent event) {
        Gson gson = new Gson();
        try {
            if (event.getSource() == lblNextEvents) {
                String serverEventResponse = ClientUtil.communicateWithServer("getNextEventsForCurrentUser", SessionInfo.getCurrentUserEmail());
                EventDataResponse eventsResponse = gson.fromJson(serverEventResponse, EventDataResponse.class);
                if ("200".equalsIgnoreCase(eventsResponse.getStatusCode())) {
                    refreshNodes(eventsResponse.getEvents());
                }
                lblNextEvents.setFont(Font.font(null, FontWeight.BOLD, 13.0));
                lblCompletedEvents.setFont(Font.font(13.0));
                lblAllEvents.setFont(Font.font(13.0));
            } else if (event.getSource() == lblCompletedEvents) {
                String serverEventResponse = ClientUtil.communicateWithServer("getCompletedEventsForCurrentUser", SessionInfo.getCurrentUserEmail());
                EventDataResponse eventsResponse = gson.fromJson(serverEventResponse, EventDataResponse.class);
                if ("200".equalsIgnoreCase(eventsResponse.getStatusCode())) {
                    refreshNodes(eventsResponse.getEvents());
                }
                lblNextEvents.setFont(Font.font(13.0));
                lblCompletedEvents.setFont(Font.font(null, FontWeight.BOLD, 13.0));
                lblAllEvents.setFont(Font.font(13.0));
            } else {
                String serverEventResponse = ClientUtil.communicateWithServer("getAllEvents", SessionInfo.getCurrentUserEmail());
                EventDataResponse eventsResponse = gson.fromJson(serverEventResponse, EventDataResponse.class);
                if ("200".equalsIgnoreCase(eventsResponse.getStatusCode())) {
                    refreshNodes(eventsResponse.getEvents());
                }
                lblNextEvents.setFont(Font.font(13.0));
                lblCompletedEvents.setFont(Font.font(13.0));
                lblAllEvents.setFont(Font.font(null, FontWeight.BOLD, 13.0));
            }
        } catch (Exception e) {
            // TO DO
        }
    }

    @FXML
    public void handleNotificationEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnNotificationEvent) {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/NotificationView.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(btnNotificationEvent.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleInvitationEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnInvitationEvent) {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/InvitationView.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(btnNotificationEvent.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleOrganizersEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnOrganizersEvent) {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/OrganizersListView.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(btnNotificationEvent.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleUsersEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnUsersEvent) {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/UsersListView.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(btnNotificationEvent.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleCustomersEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnCustomersEvent) {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getClassLoader().getResource("ui/CustomersListView.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(btnNotificationEvent.getScene().getWindow());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleCreateEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnCreateEvent) {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setMaximized(false);
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/CreateEventView.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                // TO DO
            }
        }
    }

    @FXML
    public void handleEditProfileEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnEditProfileEvent) {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setMaximized(false);
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/EditProfileView.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                // TO DO
            }
        }
    }


    @FXML
    public void handleLogoutEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnLogoutEvent) {
            try {
                SessionInfo.currentUserEmail = "Anonymous";
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setMaximized(false);
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/LoginView.fxml")));
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
                if("CUSTOMER".equalsIgnoreCase(userData.getRoles().get(0).getRoleName())) {
                    btnCreateEvent.setVisible(false);
                    btnUsersEvent.setVisible(false);
                    btnCustomersEvent.setVisible(false);
                    btnOrganizersEvent.setVisible(false);
                    lblUsersSection.setVisible(false);
                }
                lblUserName.setText(userData.getFirstName() + " " + userData.getLastName());
                lblUserRole.setText(userData.getRoles().get(0).getRoleName());
                if (userData.getAvailability()) {
                    lblSwtichAvailability.switchOnProperty().set(true);
                    lblSwtichAvailability.setText("Available");
                    lblSwtichAvailability.setStyle("-fx-background-color: green;-fx-text-fill:white;");
                    lblSwtichAvailability.setContentDisplay(ContentDisplay.RIGHT);
                } else {
                    lblSwtichAvailability.switchOnProperty().set(false);
                    lblSwtichAvailability.setText("Not Available");
                    lblSwtichAvailability.setStyle("-fx-background-color: grey;-fx-text-fill:black;");
                    lblSwtichAvailability.setContentDisplay(ContentDisplay.LEFT);
                }
                String serverEventResponse = ClientUtil.communicateWithServer("getAllEvents", SessionInfo.getCurrentUserEmail());
                EventDataResponse eventsResponse = gson.fromJson(serverEventResponse, EventDataResponse.class);
                if ("200".equalsIgnoreCase(eventsResponse.getStatusCode())) {
                    refreshNodes(eventsResponse.getEvents());
                    lblAllEvents.setFont(Font.font(null, FontWeight.BOLD, 13.0));
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void refreshNodes(List<EventData> eventDataList) {
        pnl_scroll.getChildren().clear();

        Node[] nodes = new Node[eventDataList.size()];
        for (int i = 0; i <= eventDataList.size()-1; i++) {
            try {
                nodes[i] = (Node) FXMLLoader.load(getClass().getClassLoader().getResource("ui/Event.fxml"));
                ((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(2))
                        .setText(eventDataList.get(i).getName());
                ((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(5))
                        .setText(eventDataList.get(i).getOrganizer());
                ((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(6))
                        .setText(String.valueOf(eventDataList.get(i).getNrSeats()));
                ((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(15))
                        .setText(eventDataList.get(i).getLocation());
                ((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(14))
                        .setText("Ref: " + eventDataList.get(i).getEventId());
                Date eventDate = eventDataList.get(i).getDate();
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
                cal.setTime(eventDate);
                ((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(11))
                        .setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                ((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(12))
                        .setText(EventDateUtil.getEventMonth(String.valueOf(cal.get(Calendar.MONTH))));
                ((Label) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(13))
                        .setText(String.valueOf(cal.get(Calendar.YEAR)));
                ((Label) ((Pane) ((Pane) ((AnchorPane) nodes[i]).getChildren().get(0)).getChildren().get(8))
                        .getChildren().get(0)).setText(eventDataList.get(i).getDescription());

                pnl_scroll.getChildren().add(nodes[i]);

            } catch (IOException ex) {
                // TO DO
            }
        }
    }
}
