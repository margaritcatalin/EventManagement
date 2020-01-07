package application.controllers;

import application.response.SimpleResponse;
import application.util.ClientUtil;
import application.util.SessionInfo;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EventController implements Initializable {
    @FXML
    private Button btnEditEvent;
    @FXML
    private Button btnDeleteEvent;

    @FXML
    private Label lblEventCode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void handleEditEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnEditEvent) {
            SessionInfo.selectedElementCode = lblEventCode.getText().substring(5);
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setMaximized(false);
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/EditEventView.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                // TO DO
            }
        }
    }

    @FXML
    public void handleDeleteEventButtonAction(MouseEvent event) {
        if (event.getSource() == btnDeleteEvent) {
            Gson gson = new Gson();
            String serverResponse = ClientUtil.communicateWithServer("deleteEvent", lblEventCode.getText().substring(5));
            SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
        }
    }

}
