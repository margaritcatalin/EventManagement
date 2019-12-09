package application.response;

import application.data.EventData;

import java.util.List;

public class EventDataResponse {
    String statusCode;
    List<EventData> events;

    public List<EventData> getEvents() {
        return events;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setEvents(List<EventData> events) {
        this.events = events;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
