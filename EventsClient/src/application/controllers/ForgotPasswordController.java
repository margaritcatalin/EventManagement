package application.controllers;

import application.request.AcceptInvitationRequest;
import application.request.LoginRequest;
import application.response.SimpleResponse;
import application.util.ClientUtil;
import application.util.SessionInfo;
import application.util.ValidationUtil;
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

public class ForgotPasswordController implements Initializable {

    @FXML
    private Label lblErrors;
    @FXML
    private Label lblForgotStatus;
    @FXML
    private TextField txtUsername;
    @FXML
    private Button btnEnterCode;
    @FXML
    private Button btnBackToLogin;
    @FXML
    private Button btnChangePassword;
    @FXML
    private Button btnSendEmail;
    @FXML
    private TextField txtConfirmNewPassword;
    @FXML
    private TextField txtNewPassword;

    @FXML
    public void handleSendEmailButtonAction(MouseEvent event) {

        if (event.getSource() == btnSendEmail) {
            try {
                if (txtUsername.getText().isEmpty() || !ValidationUtil.validateEmail(txtUsername.getText())) {
                    setForgotStatusLabel(Color.TOMATO, "Email invalid");
                } else {
                    SessionInfo.selectedUserEmail = txtUsername.getText();
                    String serverResponse = ClientUtil.communicateWithServer("sendForgotCode", SessionInfo.getSelectedUserEmail());
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();
                    Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/ForgotCodeView.fxml")));
                    stage.setScene(scene);
                    stage.show();
                }
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    @FXML
    public void handleEnterForgotCodeButtonAction(MouseEvent event) {

        if (event.getSource() == btnEnterCode) {
            if (StringUtils.isNoneEmpty(txtUsername.getText())) {
                AcceptInvitationRequest acceptInvitationRequest = new AcceptInvitationRequest();
                acceptInvitationRequest.setInvitationCode(txtUsername.getText());
                acceptInvitationRequest.setUserEmail(SessionInfo.getSelectedUserEmail());
                Gson gson = new Gson();
                String request = gson.toJson(acceptInvitationRequest);
                String serverResponse = ClientUtil.communicateWithServer("enterForgotCode", request);
                SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
                if ("200".equalsIgnoreCase(response.getStatusCode())) {
                    try {
                        Node node = (Node) event.getSource();
                        Stage stage = (Stage) node.getScene().getWindow();
                        stage.close();
                        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/ChangePasswordView.fxml")));
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException ex) {
                        System.err.println(ex.getMessage());
                    }
                } else {
                    setForgotStatusLabel(Color.TOMATO, "We get an error.");
                }
            } else {
                setForgotStatusLabel(Color.TOMATO, "You need to enter a valid code.");
            }
        }
    }

    @FXML
    public void handleChangePasswordButtonAction(MouseEvent event) {

        if (event.getSource() == btnChangePassword) {
            // login here
            if (ValidationUtil.validatePassword(txtNewPassword.getText(), txtConfirmNewPassword.getText())) {
                AcceptInvitationRequest acceptInvitationRequest = new AcceptInvitationRequest();
                acceptInvitationRequest.setInvitationCode(txtNewPassword.getText());
                acceptInvitationRequest.setUserEmail(SessionInfo.getSelectedUserEmail());
                Gson gson = new Gson();
                String request = gson.toJson(acceptInvitationRequest);
                String serverResponse = ClientUtil.communicateWithServer("changePassword", request);
                SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
                if ("200".equalsIgnoreCase(response.getStatusCode())) {
                    openLoginView(event);
                } else {
                    setForgotStatusLabel(Color.TOMATO, "We get an error.");
                }
            } else {
                setForgotStatusLabel(Color.TOMATO, "Password or confirm password is invalid.");
            }
        }
    }

    @FXML
    public void handleBackToLoginButtonAction(MouseEvent event) {
        if (event.getSource() == btnBackToLogin) {
            SessionInfo.selectedUserEmail = "NONE";
            openLoginView(event);
        }
    }

    private void openLoginView(MouseEvent event) {
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            // stage.setMaximized(true);
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/LoginView.fxml")));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void setForgotStatusLabel(Color color, String text) {
        lblForgotStatus.setTextFill(color);
        lblForgotStatus.setText(text);
    }
}
