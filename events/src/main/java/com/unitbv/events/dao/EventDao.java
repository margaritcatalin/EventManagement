package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.Event;

public interface EventDao extends GenericDAO<Event> {
	 void close();

	 Event createOrUpdate(Event entity);

	 Event findById(int id);

	 Event update(Event entity);

	 void delete(Event entity);

	 void deleteAll();

	 List<Event> readAll();
}
