package application.controllers;

import application.data.InvitationData;
import application.response.InvitationDataResponse;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

public class InvitationController implements Initializable {
    @FXML
    TableView tblData;
    @FXML
    private Button btnDownloadInvitation;
    @FXML
    private Button btnAcceptInvitation;
    @FXML
    private Button btnSeeEventDetails;
    private ObservableList<ObservableList> invitationData;
    @FXML
    private Label lblStatus;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetColumnList();
        fetRowList();
    }

    @FXML
    public void handleAcceptInvitationAction(MouseEvent event) {
        if (event.getSource() == btnAcceptInvitation) {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setMaximized(false);
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/AcceptInvitationView.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                // TO DO
            }
        }
    }

    @FXML
    public void handleSeeEventDetailsAction(MouseEvent event) {
        if (event.getSource() == btnSeeEventDetails && Objects.nonNull(tblData.getSelectionModel().getSelectedItem())) {
            SessionInfo.selectedInvitation = "NONE";
            ((ObservableListWrapper) tblData.getSelectionModel().getSelectedItem()).forEach(obj -> {
                if (((String) obj).contains("Ref: ")) {
                    SessionInfo.selectedInvitation = ((String) obj).substring(5);
                }
            });
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setMaximized(false);
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/EventDetailsView.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                // TO DO
            }
        } else {
            setStatusLabel(Color.TOMATO, "You need to select an event!");
        }
    }

    private void setStatusLabel(Color color, String text) {
        lblStatus.setTextFill(color);
        lblStatus.setText(text);
    }

    @FXML
    public void handleDownloadInvitationAction(MouseEvent event) {
        if (event.getSource() == btnDownloadInvitation && Objects.nonNull(tblData.getSelectionModel().getSelectedItem())) {
            SessionInfo.selectedInvitation = "NONE";
            ((ObservableListWrapper) tblData.getSelectionModel().getSelectedItem()).forEach(obj -> {
                if (((String) obj).contains("Ref: ")) {
                    SessionInfo.selectedInvitation = ((String) obj).substring(5);
                }
            });
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            stage.show();
            FileChooser fileChooser = new FileChooser();
            //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            //Show save file dialog
            File file = fileChooser.showSaveDialog(stage);
            Gson gson = new Gson();
            String serverEventResponse = ClientUtil.communicateWithServer("getInvitationByCode", SessionInfo.getSelectedInvitation());
            InvitationDataResponse invitationDataResponse = gson.fromJson(serverEventResponse, InvitationDataResponse.class);
            if ("200".equalsIgnoreCase(invitationDataResponse.getStatusCode())) {
                InvitationData invitationData = invitationDataResponse.getInvitations().get(0);
                if (file != null) {
                    String steps="Access your account and on the Invitations screen you have the acceptance button. Press it and enter the following invitation key: ";
                    saveTextToFile(steps+Base64.getEncoder().encodeToString(invitationData.getInvitationFile().getFileData()), file);
                }
            }
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
        List<String> invitationColumns = new ArrayList<>();
        invitationColumns.add("Reference");
        invitationColumns.add("Creation Date");
        invitationColumns.add("Description");
        invitationColumns.add("Event");
        invitationColumns.add("Event Date");
        invitationColumns.add("Accepted");
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
                            row.add(invitation.getReference());
                        } else if (i == 1) {
                            row.add(invitation.getCreationDate().toString());
                        } else if (i == 2) {
                            row.add(invitation.getDescription());
                        } else if (i == 3) {
                            row.add(invitation.getEventName());
                        } else if (i == 4) {
                            row.add(invitation.getEventDate().toString());
                        } else if (i == 5) {
                            row.add(invitation.getIsAccepted());
                        } else if (i == 6) {
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

    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
        }
    }
}
