package com.unitbv.events.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the invitations database table.
 * 
 */
@Entity
@Table(name="invitations")
@NamedQuery(name="Invitation.findAll", query="SELECT i FROM Invitation i")
public class Invitation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int invitationId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	private String description;

	private Boolean isAccepted;
	
	//bi-directional many-to-one association to Event
	@ManyToOne
	@JoinColumn(name="eventId")
	private Event event;

	//bi-directional many-to-one association to Invitationfile
	@ManyToOne
	@JoinColumn(name="invitationFileId")
	private InvitationFile invitationfile;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="userId")
	private User user;

	public Invitation() {
	}

	public int getInvitationId() {
		return this.invitationId;
	}

	public void setInvitationId(int invitationId) {
		this.invitationId = invitationId;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public InvitationFile getInvitationfile() {
		return this.invitationfile;
	}

	public void setInvitationfile(InvitationFile invitationfile) {
		this.invitationfile = invitationfile;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public Boolean getIsAccepted() {
		return isAccepted;
	}
	
	public void setIsAccepted(Boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

}