package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.Event;

public interface EventDao extends GenericDAO<Event> {
	public void close();

	public Event createOrUpdate(Event entity);

	public Event findById(int id);

	public Event update(Event entity);

	public void delete(Event entity);

	public void deleteAll();

	public List<Event> readAll();
}
