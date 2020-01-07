package com.unitbv.events.response;

import java.util.List;

import com.unitbv.events.data.InvitationData;

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