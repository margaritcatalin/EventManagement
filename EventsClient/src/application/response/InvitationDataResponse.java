package application.response;

import application.data.InvitationData;
import application.data.NotificationData;

import java.util.List;

public class InvitationDataResponse {
    String statusCode;
    List<InvitationData> invitations;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<InvitationData> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<InvitationData> invitations) {
        this.invitations = invitations;
    }
}
