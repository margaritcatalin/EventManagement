package application.controllers;

import application.data.EventData;
import application.request.SendInvitationRequest;
import application.response.EventDataResponse;
import application.response.SimpleResponse;
import application.util.ClientUtil;
import application.util.SessionInfo;
import com.google.gson.Gson;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class InviteUserController implements Initializable {
    @FXML
    TableView tblData;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnSendInvitation;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtDescription;

    private ObservableList<ObservableList> eventsData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetColumnList();
        fetRowList();
    }

    @FXML
    public void handleSendInvitationAction(MouseEvent event) {
        if (event.getSource() == btnSendInvitation && Objects.nonNull(tblData.getSelectionModel().getSelectedItem())) {
            SessionInfo.selectedElementCode = "NONE";
            ((ObservableListWrapper) tblData.getSelectionModel().getSelectedItem()).forEach(obj -> {
                if (((String) obj).contains("Ref: ")) {
                    SessionInfo.selectedElementCode = ((String) obj).substring(5);
                }
            });
            SendInvitationRequest sendInvitationRequest = new SendInvitationRequest();
            sendInvitationRequest.setEventCode(SessionInfo.getSelectedElementCode());
            sendInvitationRequest.setUserEmail(SessionInfo.getSelectedUserEmail());
            if (StringUtils.isNoneEmpty(txtDescription.getText())) {
                sendInvitationRequest.setInvitationMessage(txtDescription.getText());
                Gson gson = new Gson();
                String request = gson.toJson(sendInvitationRequest);
                String serverResponse = ClientUtil.communicateWithServer("sendInvitationToUser", request);
                SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
                if ("200".equalsIgnoreCase(response.getStatusCode())) {
                    setStatusLabel(Color.GREEN, "Done");
                    openUsersView(event);
                } else {
                    setStatusLabel(Color.TOMATO, "We have an error!");
                }
            } else {
                setStatusLabel(Color.TOMATO, "You need to write something!");
            }
        } else {
            setStatusLabel(Color.TOMATO, "You need to select a user!");
        }
    }

    @FXML
    public void handleBackAction(MouseEvent event) {
        if (event.getSource() == btnBack) {
            SessionInfo.selectedElementCode = "NONE";
            openUsersView(event);
        }
    }

    @FXML
    public void handleCustomerSendInvitationAction(MouseEvent event) {
        if (event.getSource() == btnSendInvitation && Objects.nonNull(tblData.getSelectionModel().getSelectedItem())) {
            SessionInfo.selectedElementCode = "NONE";
            ((ObservableListWrapper) tblData.getSelectionModel().getSelectedItem()).forEach(obj -> {
                if (((String) obj).contains("Ref: ")) {
                    SessionInfo.selectedElementCode = ((String) obj).substring(5);
                }
            });
            SendInvitationRequest sendInvitationRequest = new SendInvitationRequest();
            sendInvitationRequest.setEventCode(SessionInfo.getSelectedElementCode());
            sendInvitationRequest.setUserEmail(SessionInfo.getSelectedUserEmail());
            if (StringUtils.isNoneEmpty(txtDescription.getText())) {
                sendInvitationRequest.setInvitationMessage(txtDescription.getText());
                Gson gson = new Gson();
                String request = gson.toJson(sendInvitationRequest);
                String serverResponse = ClientUtil.communicateWithServer("sendInvitationToUser", request);
                SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
                if ("200".equalsIgnoreCase(response.getStatusCode())) {
                    setStatusLabel(Color.GREEN, "Done");
                    openCustomersView(event);
                } else {
                    setStatusLabel(Color.TOMATO, "We have an error!");
                }
            } else {
                setStatusLabel(Color.TOMATO, "You need to write something!");
            }
        } else {
            setStatusLabel(Color.TOMATO, "You need to select a user!");
        }
    }

    @FXML
    public void handleCustomerBackAction(MouseEvent event) {
        if (event.getSource() == btnBack) {
            SessionInfo.selectedElementCode = "NONE";
            openCustomersView(event);
        }
    }

    private void openUsersView(MouseEvent event) {
        SessionInfo.selectedElementCode = "NONE";
        SessionInfo.selectedUserEmail = "NONE";
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/UsersListView.fxml")));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void openCustomersView(MouseEvent event) {
        SessionInfo.selectedElementCode = "NONE";
        SessionInfo.selectedUserEmail = "NONE";
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/CustomersListView.fxml")));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //only fetch columns
    private void fetColumnList() {
        try {
            for (int i = 0; i < getColumnNames().size(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(getColumnNames().get(i));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                tblData.getColumns().removeAll(col);
                tblData.getColumns().addAll(col);
            }
        } catch (Exception e) {
            //TO DO
        }
    }

    private List<String> getColumnNames() {
        List<String> columns = new ArrayList<>();
        columns.add("Reference");
        columns.add("Event Name");
        columns.add("Event Date");
        columns.add("Organizer");
        columns.add("Number of Seats");
        columns.add("Location");
        columns.add("Description");
        return columns;
    }

    //fetches rows and data from the list
    private void fetRowList() {
        eventsData = FXCollections.observableArrayList();
        try {
            Gson gson = new Gson();
            String serverEventResponse = ClientUtil.communicateWithServer("getNextEventsForCurrentUser", SessionInfo.getCurrentUserEmail());
            EventDataResponse eventsResponse = gson.fromJson(serverEventResponse, EventDataResponse.class);
            if ("200".equalsIgnoreCase(eventsResponse.getStatusCode())) {
                List<EventData> eventDataList = eventsResponse.getEvents();
                eventDataList.forEach(ev -> {
                    ObservableList row = FXCollections.observableArrayList();
                    for (int i = 0; i <= getColumnNames().size(); i++) {
                        if (i == 0) {
                            row.add("Ref: " + ev.getEventId());
                        }
                        if (i == 1) {
                            row.add(ev.getName());
                        } else if (i == 2) {
                            row.add(ev.getDate().toString());
                        } else if (i == 3) {
                            row.add(ev.getOrganizer());
                        } else if (i == 4) {
                            row.add(String.valueOf(ev.getNrSeats()));
                        } else if (i == 5) {
                            row.add(ev.getLocation());
                        } else if (i == 6) {
                            row.add(ev.getDescription());
                        }
                    }
                    eventsData.add(row);
                });
                tblData.setItems(eventsData);
            }
        } catch (Exception ex) {
            //TO DO
        }
    }

    private void setStatusLabel(Color color, String text) {
        lblStatus.setTextFill(color);
        lblStatus.setText(text);
    }
}


