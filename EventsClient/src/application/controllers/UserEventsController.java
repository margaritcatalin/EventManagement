package application.controllers;

import application.data.EventData;
import application.response.EventDataResponse;
import application.util.ClientUtil;
import application.util.SessionInfo;
import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserEventsController implements Initializable {
    @FXML
    TableView tblData;
    @FXML
    private Button btnBack;

    @FXML
    private Label lblStatus;

    private ObservableList<ObservableList> eventsData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetColumnList();
        fetRowList();
    }

    @FXML
    public void handleBackAction(MouseEvent event) {
        if (event.getSource() == btnBack) {
            SessionInfo.selectedElementCode = "NONE";
            openOrganizersView(event);
        }
    }

    private void openOrganizersView(MouseEvent event) {
        SessionInfo.selectedElementCode = "NONE";
        SessionInfo.selectedUserEmail = "NONE";
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/OrganizersListView.fxml")));
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
            String serverEventResponse = ClientUtil.communicateWithServer("getAllEvents", SessionInfo.getSelectedUserEmail());
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