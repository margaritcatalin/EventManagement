package application.data;

import java.util.Date;

public class InvitationData {
    private Date creationDate;
    private String description;
    private String eventName;
    private Date eventDate;
    private InvitationFileData invitationFile;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public InvitationFileData getInvitationFile() {
        return invitationFile;
    }

    public void setInvitationFile(InvitationFileData invitationFile) {
        this.invitationFile = invitationFile;
    }
}
