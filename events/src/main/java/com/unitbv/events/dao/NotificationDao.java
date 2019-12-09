package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.Notification;

public interface NotificationDao extends GenericDAO<Notification> {
	public void close();

	public Notification createOrUpdate(Notification entity);

	public Notification findById(int id);

	public Notification update(Notification entity);

	public void delete(Notification entity);

	public void deleteAll();

	public List<Notification> readAll();
}
