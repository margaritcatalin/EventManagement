package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.InvitationFile;

public interface InvitationFileDao extends GenericDAO<InvitationFile> {
	 void close();

	 InvitationFile createOrUpdate(InvitationFile entity);

	 InvitationFile findById(int id);

	 InvitationFile update(InvitationFile entity);

	 void delete(InvitationFile entity);

	 void deleteAll();

	 List<InvitationFile> readAll();
}
