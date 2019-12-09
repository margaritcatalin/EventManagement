package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.Invitation;

public interface InvitationDao extends GenericDAO<Invitation> {
	public void close();

	public Invitation createOrUpdate(Invitation entity);

	public Invitation findById(int id);

	public Invitation update(Invitation entity);

	public void delete(Invitation entity);

	public void deleteAll();

	public List<Invitation> readAll();
}
