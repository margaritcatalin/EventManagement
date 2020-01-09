package application.controllers;

import application.data.NotificationData;
import application.response.NotificationDataResponse;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {
    @FXML
    TableView tblData;
    @FXML
    private Button btnDeleteAllNotification;
    @FXML
    private Button btnDeleteNotification;
    private ObservableList<ObservableList> notificationData;
    @FXML
    private Label lblStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetColumnList();
        fetRowList();
    }

    @FXML
    public void handleDeleteAllNotificationAction(MouseEvent event) {
        if (event.getSource() == btnDeleteAllNotification) {
            String serverResponse = ClientUtil.communicateWithServer("deleteAllNotification", SessionInfo.getCurrentUserEmail());
            Gson gson = new Gson();
            SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
            if ("200".equalsIgnoreCase(response.getStatusCode())) {
                setStatusLabel(Color.GREEN, "Done");
            } else {
                setStatusLabel(Color.TOMATO, "We have an error!");
            }
            fetRowList();
        }
    }

    @FXML
    public void handleDeleteNotificationAction(MouseEvent event) {
        if (event.getSource() == btnDeleteNotification && Objects.nonNull(tblData.getSelectionModel().getSelectedItem())) {
            SessionInfo.selectedNotification = "NONE";
            ((ObservableListWrapper) tblData.getSelectionModel().getSelectedItem()).forEach(obj -> {
                if (((String) obj).contains("Ref: ")) {
                    SessionInfo.selectedNotification = ((String) obj).substring(5);
                }
            });
            String serverResponse = ClientUtil.communicateWithServer("deleteNotification", SessionInfo.getSelectedNotification());
            Gson gson = new Gson();
            SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
            if ("200".equalsIgnoreCase(response.getStatusCode())) {
                setStatusLabel(Color.GREEN, "Done");
            } else {
                setStatusLabel(Color.TOMATO, "We have an error!");
            }
            fetRowList();
        } else {
            setStatusLabel(Color.TOMATO, "You need to select a user!");
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
        columns.add("Notification message");
        return columns;
    }

    //fetches rows and data from the list
    private void fetRowList() {
        notificationData = FXCollections.observableArrayList();
        try {
            Gson gson = new Gson();
            String serverEventResponse = ClientUtil.communicateWithServer("getNotificationForCurrentUser", SessionInfo.getCurrentUserEmail());
            NotificationDataResponse notificationDataResponse = gson.fromJson(serverEventResponse, NotificationDataResponse.class);
            if ("200".equalsIgnoreCase(notificationDataResponse.getStatusCode())) {
                List<NotificationData> notificationDataList = notificationDataResponse.getNotifications();
                notificationDataList.forEach(not -> {
                    ObservableList row = FXCollections.observableArrayList();
                    for (int i = 0; i <= getColumnNames().size(); i++) {
                        if (i == 0) {
                            row.add("Ref: " + not.getNotificationId());
                        } else if (i == 1) {
                            row.add(not.getDescription());
                        }
                    }
                    notificationData.add(row);
                });
                tblData.setItems(notificationData);
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
