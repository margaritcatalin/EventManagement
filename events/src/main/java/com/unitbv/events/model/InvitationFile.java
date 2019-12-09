package com.unitbv.events.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * The persistent class for the InvitationFile database table.
 * 
 */
@Entity
@NamedQuery(name = "InvitationFile.findAll", query = "SELECT i FROM InvitationFile i")
public class InvitationFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int invitationFileId;

	private String extension;

	private byte[] fileData;

	// bi-directional many-to-one association to Invitation
	@OneToMany(mappedBy = "invitationfile")
	private List<Invitation> invitations;

	public InvitationFile() {
	}

	public int getInvitationFileId() {
		return this.invitationFileId;
	}

	public void setInvitationFileId(int invitationFileId) {
		this.invitationFileId = invitationFileId;
	}

	public String getExtension() {
		return this.extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public byte[] getFileData() {
		return this.fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public List<Invitation> getInvitations() {
		return this.invitations;
	}

	public void setInvitations(List<Invitation> invitations) {
		this.invitations = invitations;
	}

	public Invitation addInvitations(Invitation invitations) {
		getInvitations().add(invitations);
		invitations.setInvitationfile(this);

		return invitations;
	}

	public Invitation removeInvitations(Invitation invitations) {
		getInvitations().remove(invitations);
		invitations.setInvitationfile(null);

		return invitations;
	}

}