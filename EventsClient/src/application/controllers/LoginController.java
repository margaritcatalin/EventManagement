package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

/**
 * @author Catalin
 *
 */
public class LoginController implements Initializable {

	@FXML
	private Label lblErrors;

	@FXML
	private TextField txtUsername;

	@FXML
	private TextField txtPassword;

	@FXML
	private Button btnSignin;

	@FXML
	private Button btnSignup;

	@FXML
	public void handleLoginButtonAction(MouseEvent event) {

		if (event.getSource() == btnSignin) {
			// login here
			if (logIn().equals("Success")) {
				try {
					SessionInfo.currentUserEmail = txtUsername.getText();
					Node node = (Node) event.getSource();
					Stage stage = (Stage) node.getScene().getWindow();
					stage.setMaximized(true);
					stage.close();
					Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/AdminHomeView.fxml")));
					stage.setScene(scene);
					stage.show();
				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}

			}
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
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("../ui/RegistrationView.fxml")));
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

}
