package application.controllers;

import application.request.AcceptInvitationRequest;
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AcceptInvitationController implements Initializable {
    @FXML
    private Button btnBack;
    @FXML
    private Button btnAcceptInvitation;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtDescription;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void handleAcceptInvitationAction(MouseEvent event) {
        if (event.getSource() == btnAcceptInvitation) {
            if (StringUtils.isNoneEmpty(txtDescription.getText())) {
                AcceptInvitationRequest acceptInvitationRequest = new AcceptInvitationRequest();
                acceptInvitationRequest.setInvitationCode(txtDescription.getText());
                acceptInvitationRequest.setUserEmail(SessionInfo.getCurrentUserEmail());
                Gson gson = new Gson();
                String request = gson.toJson(acceptInvitationRequest);
                String serverResponse = ClientUtil.communicateWithServer("acceptInvitation", request);
                SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
                if ("200".equalsIgnoreCase(response.getStatusCode())) {
                    setStatusLabel(Color.GREEN, "Done");
                    openInvitationView(event);
                } else {
                    setStatusLabel(Color.TOMATO, response.getMessage());
                }
            } else {
                setStatusLabel(Color.TOMATO, "You need to enter a key!");
            }
        }
    }

    @FXML
    public void handleBackAction(MouseEvent event) {
        if (event.getSource() == btnBack) {
            SessionInfo.selectedElementCode = "NONE";
            openInvitationView(event);
        }
    }

    private void openInvitationView(MouseEvent event) {
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/InvitationView.fxml")));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void setStatusLabel(Color color, String text) {
        lblStatus.setTextFill(color);
        lblStatus.setText(text);
    }

}
