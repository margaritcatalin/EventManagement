package application.controllers;

import application.data.NotificationData;
import application.response.NotificationDataResponse;
import application.util.ClientUtil;
import application.util.SessionInfo;
import com.google.gson.Gson;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {
    @FXML
    TableView tblData;

    private ObservableList<ObservableList> notificationData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //only fetch columns
    private void fetColumnList() {

        try {
            TableColumn col = new TableColumn("Notification text");
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(0).toString());
                }
            });

            tblData.getColumns().removeAll(col);
            tblData.getColumns().addAll(col);

        } catch (Exception e) {
            //TO DO
        }
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
                ObservableList row = FXCollections.observableArrayList();
                notificationDataList.forEach(not -> row.add(not.getDescription()));
                notificationData.add(row);
                tblData.setItems(notificationData);
            }
        } catch (Exception ex) {
            //TO DO
        }
    }
}
}
