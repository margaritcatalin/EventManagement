package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.request.AcceptInvitationRequest;
import application.util.ValidationUtil;
import com.google.gson.Gson;

import application.request.LoginRequest;
import application.response.SimpleResponse;
import application.util.ClientUtil;
import application.util.SessionInfo;
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

/**
 * @author Catalin
 */
public class LoginController implements Initializable {

    @FXML
    private Label lblErrors;
    @FXML
    private Label lblForgotStatus;
    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnSignin;
    @FXML
    private Label btnForgot;
    @FXML
    private Button btnSignup;
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
    public void handleLoginButtonAction(MouseEvent event) {

        if (event.getSource() == btnSignin) {
            // login here
            if (logIn().equals("Success")) {
                try {
                    SessionInfo.currentUserEmail = txtUsername.getText();
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    //stage.setMaximized(true);
                    stage.close();
                    Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/AdminHomeView.fxml")));
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }

            }
        }
    }

    @FXML
    public void handleForgotButtonAction(MouseEvent event) {

        if (event.getSource() == btnForgot) {
            // login here
            try {
                SessionInfo.currentUserEmail = txtUsername.getText();
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/ForgotView.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

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
                    Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/ForgotCodeView.fxml")));
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
                        Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/ChangePasswordView.fxml")));
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
            if (!ValidationUtil.validatePassword(txtNewPassword.getText(), txtConfirmNewPassword.getText())) {
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
            Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/LoginView.fxml")));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    public void handleRegisterButtonAction(MouseEvent event) {

        if (event.getSource() == btnSignup) {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                // stage.setMaximized(true);
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("ui/RegistrationView.fxml")));
                stage.setScene(scene);
                stage.show();

            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblErrors.setTextFill(Color.GREEN);
        lblErrors.setText("Server is up : Good to go");
    }

    private String logIn() {
        String status = "Success";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(txtUsername.getText());
        loginRequest.setPassword(txtPassword.getText());
        if (loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()
                || !ValidationUtil.validateEmail((loginRequest.getEmail()))) {
            setLblError(Color.TOMATO, "Invalid credentials");
            status = "Error";
        } else {
            Gson gson = new Gson();
            try {
                String request = gson.toJson(loginRequest);
                String serverResponse = ClientUtil.communicateWithServer("login", request);
                SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
                if (!"200".equalsIgnoreCase(response.getStatusCode())) {
                    setLblError(Color.TOMATO, "Enter Correct Email/Password");
                    status = "Error";
                } else {
                    setLblError(Color.GREEN, "Login Successful..Redirecting..");
                }
            } catch (Exception e) {
                status = "Error";
                lblErrors.setTextFill(Color.TOMATO);
                lblErrors.setText("Server Error : Check");
            }
        }

        return status;
    }

    private void setLblError(Color color, String text) {
        lblErrors.setTextFill(color);
        lblErrors.setText(text);
    }

    private void setForgotStatusLabel(Color color, String text) {
        lblForgotStatus.setTextFill(color);
        lblForgotStatus.setText(text);
    }
}
