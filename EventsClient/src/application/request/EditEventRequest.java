package application.request;

import java.util.Date;

public class EditEventRequest {

    private String name;
    private String location;
    private String description;
    private String eventId;
    private Integer nrSeats;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Integer getNrSeats() {
        return nrSeats;
    }

    public void setNrSeats(Integer nrSeats) {
        this.nrSeats = nrSeats;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

