package application.controllers;

import application.data.CustomerData;
import application.response.CustomerDataResponse;
import application.util.ClientUtil;
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
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
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
            for (int i = 0; i <= getColumnNames().size(); i++) {
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
        invitationData = FXCollections.observableArrayList();
        try {
            Gson gson = new Gson();
            String serverEventResponse = ClientUtil.communicateWithServer("getAllCustomers", "");
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
                    invitationData.add(row);
                });
                tblData.setItems(invitationData);
            }
        } catch (Exception ex) {
            //TO DO
        }
    }
}

