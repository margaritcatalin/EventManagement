package application.response;

import application.data.NotificationData;

import java.util.List;

public class NotificationDataResponse {
    String statusCode;
    List<NotificationData> notifications;

    public List<NotificationData> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationData> notifications) {
        this.notifications = notifications;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}