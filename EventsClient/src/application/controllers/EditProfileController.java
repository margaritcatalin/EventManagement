package application.controllers;

import application.data.UserData;
import application.request.EditProfileRequest;
import application.request.GetUserRequst;
import application.response.SimpleResponse;
import application.response.UserDataResponse;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditProfileController implements Initializable {
    @FXML
    private TextField txtFirstname;

    @FXML
    private TextField txtLastname;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtConfirmPassword;

    @FXML
    private Button btnEditProfile;

    @FXML
    private Button btnBack;

    @FXML
    private Label emailLblError;

    @FXML
    private Label passowrdLblError;

    @FXML
    private Label serverLblError;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GetUserRequst getUserRequst = new GetUserRequst();
        getUserRequst.setEmail(SessionInfo.getCurrentUserEmail());
        Gson gson = new Gson();
        try {
            String request = gson.toJson(getUserRequst);
            String serverResponse = ClientUtil.communicateWithServer("getUser", request);
            UserDataResponse response = gson.fromJson(serverResponse, UserDataResponse.class);
            if ("200".equalsIgnoreCase(response.getStatusCode())) {
                UserData userData = response.getUserData();
                txtFirstname.setText(userData.getFirstName());
                txtLastname.setText(userData.getLastName());
                txtEmail.setText(userData.getEmail());
            }
        } catch (Exception e) {
            // TO DO
        }
    }

    @FXML
    public void handleEditProfileEventButtonAction(MouseEvent event) {

        if (event.getSource() == btnEditProfile && "Success".equalsIgnoreCase(editProfile(event))) {
            openAdminView(event);
        }
    }

    @FXML
    public void handleBackButtonAction(MouseEvent event) {
        if (event.getSource() == btnBack) {
            openAdminView(event);
        }
    }

    private void openAdminView(MouseEvent event) {
        try {
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            // stage.setMaximized(true);
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/AdminHomeView.fxml")));
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private String editProfile(MouseEvent event) {
        String status = "Success";
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        if (email.isEmpty() || !ValidationUtil.validateEmail(email)) {
            setEmailLblError(Color.TOMATO, "Email invalid");
            status = "Error";
        } else if (!password.equals(confirmPassword)) {
            setPassowrdLblError(Color.TOMATO, "Password or confirm password is invalid");
            status = "Error";
        } else {
            Gson gson = new Gson();
            EditProfileRequest editProfileRequest = new EditProfileRequest();
            editProfileRequest.setEmail(email);
            editProfileRequest.setCurrentEmail(SessionInfo.getCurrentUserEmail());
            editProfileRequest.setFirstName(txtFirstname.getText());
            editProfileRequest.setLastName(txtLastname.getText());
            editProfileRequest.setNewPassword(password);
            try {
                String request = gson.toJson(editProfileRequest);
                String serverResponse = ClientUtil.communicateWithServer("editProfile", request);
                SimpleResponse response = gson.fromJson(serverResponse, SimpleResponse.class);
                if ("301".equalsIgnoreCase(response.getStatusCode())) {
                    setEmailLblError(Color.TOMATO, response.getMessage());
                    status = "Error";
                }
                if ("500".equalsIgnoreCase(response.getStatusCode())) {
                    setServerLblError(Color.TOMATO, response.getMessage());
                    status = "Error";
                }
            } catch (Exception e) {
                status = "Error";
                setServerLblError(Color.TOMATO, "Internal Server Error");
            }
        }

        return status;
    }

    private void setServerLblError(Color color, String text) {
        serverLblError.setTextFill(color);
        serverLblError.setText(text);
    }

    private void setEmailLblError(Color color, String text) {
        emailLblError.setTextFill(color);
        emailLblError.setText(text);
    }

    private void setPassowrdLblError(Color color, String text) {
        passowrdLblError.setTextFill(color);
        passowrdLblError.setText(text);
    }

}
