package com.unitbv.events.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.unitbv.events.dao.EventDao;
import com.unitbv.events.dao.UserDao;
import com.unitbv.events.data.EventData;
import com.unitbv.events.model.Event;
import com.unitbv.events.model.User;
import com.unitbv.events.request.CreateEventRequest;
import com.unitbv.events.response.EventDataResponse;
import com.unitbv.events.service.EventService;
import com.unitbv.events.util.EntityDAOImplFactory;

public class DefaultEventService implements EventService {
	private static final String PERSISTENCE_UNIT_NAME = "events";
	private EntityDAOImplFactory entityDAOImplFactory;
	private UserDao userDao;
	private EventDao eventDao;

	public DefaultEventService() {
		userDao = entityDAOImplFactory.createNewUserDao(PERSISTENCE_UNIT_NAME);
		eventDao = entityDAOImplFactory.createNewEventDao(PERSISTENCE_UNIT_NAME);

	}

	@Override
	public EventDataResponse getAllEvents() {
		EventDataResponse response = new EventDataResponse();
		List<Event> eventModels = eventDao.readAll();
		List<EventData> events = new ArrayList<EventData>();
		eventModels.stream().forEach(event -> {
			EventData eventData = new EventData();
			eventData.setDate(event.getDate());
			eventData.setName(event.getName());
			eventData.setLocation(event.getLocation());
			eventData.setDescription(event.getDescription());
			eventData.setNrSeats(event.getNrSeats());
			String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
			eventData.setOrganizer(organizer);
			events.add(eventData);
		});
		response.setEvents(events);
		response.setStatusCode("200");
		return response;
	}

	@Override
	public boolean createEvent(CreateEventRequest request) {
		Event event = new Event();
		event.setName(request.getName());
		event.setDescription(request.getDescription());
		event.setLocation(request.getLocation());
		event.setNrSeats(request.getNrSeats());
		event.setDate(request.getDate());
		User user = userDao.findByEmail(request.getOrganizer());
		event.setUser(user);
		return Objects.nonNull(eventDao.createOrUpdate(event));
	}

}
