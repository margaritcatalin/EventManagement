package application.controllers;

import application.data.CustomerData;
import application.data.UserData;
import application.request.GetUserRequst;
import application.response.CustomerDataResponse;
import application.response.SimpleResponse;
import application.response.UserDataResponse;
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
import javafx.scene.Parent;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    @FXML
    TableView tblData;
    @FXML
    private Button btnSendInvitation;
    @FXML
    private Button btnDeactivateUser;
    @FXML
    private Button btnInviteAll;
    @FXML
    private Label lblStatus;

    private ObservableList<ObservableList> usersData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GetUserRequst getUserRequst = new GetUserRequst();
        getUserRequst.setEmail(SessionInfo.getCurrentUserEmail());
        Gson gson = new Gson();
        String request = gson.toJson(getUserRequst);
        String serverResponse = ClientUtil.communicateWithServer("getUser", request);
        UserDataResponse response = gson.fromJson(serverResponse, UserDataResponse.class);
        if ("200".equalsIgnoreCase(response.getStatusCode())) {
            UserData userData = response.getUserData();
            if (!"ADMIN".equalsIgnoreCase(userData.getRoles().get(0).getRoleName())) {
                btnDeactivateUser.setVisible(false);
            }
        }
        fetColumnList();
        fetRowList();
    }

    @FXML
    public void handleSendInvitationAction(MouseEvent event) {
        if (event.getSource() == btnSendInvitation && Objects.nonNull(tblData.getSelectionModel().getSelectedItem())) {
            SessionInfo.selectedUserEmail = "NONE";
            ((ObservableListWrapper) tblData.getSelectionModel().getSelectedItem()).forEach(obj -> {
                if (((String) obj).contains("@")) {
                    SessionInfo.selectedUserEmail = (String) obj;
                }
            });
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setMaximized(false);
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/InviteUserEventView.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                // TO DO
            }
        } else {
            setStatusLabel(Color.TOMATO, "You need to select a user!");
        }
    }

    @FXML
    public void handleDeactivateUserAction(MouseEvent event) {
        if (event.getSource() == btnDeactivateUser && Objects.nonNull(tblData.getSelectionModel().getSelectedItem())) {
            SessionInfo.selectedUserEmail = "NONE";
            ((ObservableListWrapper) tblData.getSelectionModel().getSelectedItem()).forEach(obj -> {
                if (((String) obj).contains("@")) {
                    SessionInfo.selectedUserEmail = (String) obj;
                }
            });
            String serverResponse = ClientUtil.communicateWithServer("deactivateUser", SessionInfo.selectedUserEmail);
            Gson gson = new Gson();
            SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
            if ("200".equalsIgnoreCase(response.getStatusCode())) {
                setStatusLabel(Color.GREEN, "Done");
                fetRowList();
            } else {
                setStatusLabel(Color.TOMATO, "We have an error!");
            }
        } else {
            setStatusLabel(Color.TOMATO, "You need to select a user!");
        }
    }

    @FXML
    public void handleInviteAllAction(MouseEvent event) {
        if (event.getSource() == btnInviteAll) {
            SessionInfo.selectedUserEmail ="All";
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setMaximized(false);
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/InviteUserEventView.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                // TO DO
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
        List<String> columns = new ArrayList<>();
        columns.add("First Name");
        columns.add("Last Name");
        columns.add("Email");
        columns.add("User Status");
        columns.add("Availability Status");
        return columns;
    }

    //fetches rows and data from the list
    private void fetRowList() {
        usersData = FXCollections.observableArrayList();
        try {
            Gson gson = new Gson();
            String serverEventResponse = ClientUtil.communicateWithServer("getAllUsers", "");
            CustomerDataResponse customerDataResponse = gson.fromJson(serverEventResponse, CustomerDataResponse.class);
            if ("200".equalsIgnoreCase(customerDataResponse.getStatusCode())) {
                List<CustomerData> customerDataList = customerDataResponse.getCustomers();
                customerDataList.forEach(customer -> {
                    ObservableList row = FXCollections.observableArrayList();
                    for (int i = 0; i <= getColumnNames().size(); i++) {
                        if (i == 0) {
                            row.add(customer.getFirstName());
                        } else if (i == 1) {
                            row.add(customer.getLastName());
                        } else if (i == 2) {
                            row.add(customer.getEmail());
                        } else if (i == 3) {
                            row.add(customer.getUserStatus());
                        } else if (i == 4) {
                            row.add(customer.getAvailabilityStatus());
                        }
                    }
                    usersData.add(row);
                });
                tblData.setItems(usersData);

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

