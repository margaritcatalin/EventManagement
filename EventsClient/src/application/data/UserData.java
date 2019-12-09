package application.data;

import java.util.List;

public class UserData {
    String email;
    String firstName;
    String lastName;
    List<RoleData> roles;
    List<EventData> events;
    List<InvitationData> invitations;
    List<NotificationData> notifications;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public List<RoleData> getRoles() {
        return roles;
    }
    public void setRoles(List<RoleData> roles) {
        this.roles = roles;
    }
    public List<EventData> getEvents() {
        return events;
    }
    public void setEvents(List<EventData> events) {
        this.events = events;
    }
    public List<InvitationData> getInvitations() {
        return invitations;
    }
    public void setInvitations(List<InvitationData> invitations) {
        this.invitations = invitations;
    }
    public List<NotificationData> getNotifications() {
        return notifications;
    }
    public void setNotifications(List<NotificationData> notifications) {
        this.notifications = notifications;
    }


}
