package application.util;

import application.data.UserData;
import application.request.AvailabilityRequest;
import application.request.GetUserRequst;
import application.response.SimpleResponse;
import application.response.UserDataResponse;
import com.google.gson.Gson;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

public class AvailableSwitchButton extends Label {
    private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);

    public AvailableSwitchButton() {
        Button switchBtn = new Button();
        switchBtn.setPrefWidth(40);
        switchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                switchedOn.set(!switchedOn.get());
            }
        });

        setGraphic(switchBtn);

        switchedOn.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean t, Boolean t1) {
                GetUserRequst getUserRequst = new GetUserRequst();
                getUserRequst.setEmail(SessionInfo.getCurrentUserEmail());
                Gson gson = new Gson();
                try {
                    String request = gson.toJson(getUserRequst);
                    String serverResponse = ClientUtil.communicateWithServer("getUser", request);
                    UserDataResponse response = gson.fromJson(serverResponse, UserDataResponse.class);
                    if ("200".equalsIgnoreCase(response.getStatusCode())) {
                        UserData userData = response.getUserData();
                        AvailabilityRequest avRequest = new AvailabilityRequest();
                        avRequest.setCurrentEmail(SessionInfo.getCurrentUserEmail());
                        if (t1) {
                            setText("Available");
                            setStyle("-fx-background-color: green;-fx-text-fill:white;");
                            setContentDisplay(ContentDisplay.RIGHT);
                            avRequest.setAvailable(true);

                        } else {
                            setText("Not Available");
                            setStyle("-fx-background-color: grey;-fx-text-fill:black;");
                            setContentDisplay(ContentDisplay.LEFT);
                            avRequest.setAvailable(false);
                        }
                        String availabilityRequest = gson.toJson(avRequest);
                        String avServerResponse = ClientUtil.communicateWithServer("setUserAvailability", availabilityRequest);
                        SimpleResponse avResponse = gson.fromJson(avServerResponse, SimpleResponse.class);
                    }
                } catch (Exception e) {
                    // TO DO
                }
            }
        });

    }

    public SimpleBooleanProperty switchOnProperty() {
        return switchedOn;
    }

}