package application.controllers;

import application.data.InvitationData;
import application.response.InvitationDataResponse;
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
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class InvitationController implements Initializable {
    @FXML
    TableView tblData;

    private ObservableList<ObservableList> invitationData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetColumnList();
        fetRowList();
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
        List<String> invitationColumns = new ArrayList<>();
        invitationColumns.add("Creation Date");
        invitationColumns.add("Description");
        invitationColumns.add("Event");
        invitationColumns.add("Event Date");
        invitationColumns.add("Attachments");
        return invitationColumns;
    }

    private void fetRowList() {
        invitationData = FXCollections.observableArrayList();
        try {
            Gson gson = new Gson();
            String serverEventResponse = ClientUtil.communicateWithServer("getInvitationsForCurrentUser", SessionInfo.getCurrentUserEmail());
            InvitationDataResponse invitationDataResponse = gson.fromJson(serverEventResponse, InvitationDataResponse.class);
            if ("200".equalsIgnoreCase(invitationDataResponse.getStatusCode())) {
                List<InvitationData> invitationDataList = invitationDataResponse.getInvitations();
                invitationDataList.forEach(invitation -> {
                    ObservableList row = FXCollections.observableArrayList();
                    for (int i = 0; i <= getColumnNames().size(); i++) {
                        if (i == 0) {
                            row.add(invitation.getCreationDate().toString());
                        } else if (i == 1) {
                            row.add(invitation.getDescription());
                        } else if (i == 2) {
                            row.add(invitation.getEventName());
                        } else if (i == 3) {
                            row.add(invitation.getEventDate().toString());
                        } else if (i == 4) {
                            if (Objects.nonNull(invitation.getInvitationFile())) {
                                row.add("Yes");
                            } else {
                                row.add("No");
                            }
                        }
                    }
                    invitationData.add(row);
                });
                tblData.setItems(invitationData);
            }
        } catch (Exception ex) {
            //TO DO
        }
    }
}
