package com.unitbv.events.data;

import java.util.Date;

public class InvitationData {
	private String reference;
	private Date creationDate;
	private String description;
	private String eventName;
	private Date eventDate;
	private InvitationFileData invitationFile;
	private String isAccepted;

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

	public String getIsAccepted() {
		return isAccepted;
	}

	public void setIsAccepted(String isAccepted) {
		this.isAccepted = isAccepted;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
}
