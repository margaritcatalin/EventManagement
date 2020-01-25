package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.gson.Gson;

import application.request.RegisterRequest;
import application.response.SimpleResponse;
import application.util.ClientUtil;
import application.util.ValidationUtil;
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

public class RegisterController implements Initializable {
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
	private Button btnRegister;

	@FXML
	private Button btnBack;

	@FXML
	private Label emailLblError;

	@FXML
	private Label passowrdLblError;

	@FXML
	private Label serverLblError;

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	@FXML
	public void handleBackButtonAction(MouseEvent event) {

		if (event.getSource() == btnBack) {
			openLoginView(event);
		}
	}

	@FXML
	public void handleRegisterButtonAction(MouseEvent event) {

		if (event.getSource() == btnRegister && "Success".equalsIgnoreCase(register(event))) {
			openLoginView(event);
		}
	}

	private String register(MouseEvent event) {
		String status = "Success";
		String email = txtEmail.getText();
		String password = txtPassword.getText();
		String confirmPassword = txtConfirmPassword.getText();
		if (email.isEmpty() || !ValidationUtil.validateEmail(email)) {
			setEmailLblError(Color.TOMATO, "Email invalid");
			status = "Error";
		} else if (!ValidationUtil.validatePassword(password, confirmPassword)) {
			setPassowrdLblError(Color.TOMATO, "Password or confirm password is invalid");
			status = "Error";
		} else {
			Gson gson = new Gson();
			RegisterRequest registerRequest = new RegisterRequest();
			registerRequest.setEmail(email);
			registerRequest.setFirstName(txtFirstname.getText());
			registerRequest.setLastName(txtLastname.getText());
			registerRequest.setPassword(password);
			try {
				String request = gson.toJson(registerRequest);
				String serverResponse = ClientUtil.communicateWithServer("register", request);
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
}
