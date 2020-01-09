package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.Event;
import com.unitbv.events.model.Invitation;
import com.unitbv.events.model.Notification;
import com.unitbv.events.model.User;

public interface InvitationDao extends GenericDAO<Invitation> {
	void close();

	Invitation createOrUpdate(Invitation entity);

	Invitation findById(int id);

	Invitation update(Invitation entity);

	void delete(Invitation entity);

	void deleteAll();

	List<Invitation> readAll();

	List<Invitation> findByEventAndUser(User user, Event event);
	
	List<Invitation> findByUser(User user);
}
