package application.response;

import application.data.UserData;

public class UserDataResponse {

    String statusCode;
    UserData userData;

    public String getStatusCode() {
        return statusCode;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
