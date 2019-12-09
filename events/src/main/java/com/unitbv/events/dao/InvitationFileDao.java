package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.InvitationFile;

public interface InvitationFileDao extends GenericDAO<InvitationFile> {
	public void close();

	public InvitationFile createOrUpdate(InvitationFile entity);

	public InvitationFile findById(int id);

	public InvitationFile update(InvitationFile entity);

	public void delete(InvitationFile entity);

	public void deleteAll();

	public List<InvitationFile> readAll();
}
