package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.Notification;
import com.unitbv.events.model.User;

public interface NotificationDao extends GenericDAO<Notification> {
	void close();

	Notification createOrUpdate(Notification entity);

	Notification findById(int id);

	Notification update(Notification entity);

	void delete(Notification entity);

	void deleteAll();

	List<Notification> readAll();

	List<Notification> findByUser(User user);
}
