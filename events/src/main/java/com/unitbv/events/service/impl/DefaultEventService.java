package com.unitbv.events.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.unitbv.events.dao.EventDao;
import com.unitbv.events.dao.InvitationDao;
import com.unitbv.events.dao.RoleDao;
import com.unitbv.events.dao.UserDao;
import com.unitbv.events.data.EventData;
import com.unitbv.events.model.Event;
import com.unitbv.events.model.Invitation;
import com.unitbv.events.model.Role;
import com.unitbv.events.model.User;
import com.unitbv.events.request.CreateEventRequest;
import com.unitbv.events.request.EditEventRequest;
import com.unitbv.events.response.EventDataResponse;
import com.unitbv.events.response.SimpleResponse;
import com.unitbv.events.service.EventService;
import com.unitbv.events.service.InvitationService;
import com.unitbv.events.service.NotificationService;
import com.unitbv.events.util.EntityDAOImplFactory;

public class DefaultEventService implements EventService {
	private static final String PERSISTENCE_UNIT_NAME = "events";
	private EntityDAOImplFactory entityDAOImplFactory;
	private UserDao userDao;
	private EventDao eventDao;
	private RoleDao roleDao;
	private InvitationDao invitationDao;
	private NotificationService notificationService;
	private InvitationService invitationService;

	public DefaultEventService() {
		userDao = entityDAOImplFactory.createNewUserDao(PERSISTENCE_UNIT_NAME);
		eventDao = entityDAOImplFactory.createNewEventDao(PERSISTENCE_UNIT_NAME);
		roleDao = entityDAOImplFactory.createNewRoleDao(PERSISTENCE_UNIT_NAME);
		invitationDao = entityDAOImplFactory.createNewInvitationDao(PERSISTENCE_UNIT_NAME);
		notificationService = new DefaultNotificationService();
		invitationService = new DefaultInvitationService();
	}

	@Override
	public EventDataResponse getAllEvents(String currentUserEmail) {
		User userModel = userDao.findByEmail(currentUserEmail);
		EventDataResponse response = new EventDataResponse();
		List<Event> eventModels = eventDao.readAll();
		List<EventData> events = new ArrayList<EventData>();
		if (userModel.getRoles().get(0).getRoleName().equalsIgnoreCase("ADMIN")) {
			eventModels.stream().forEach(event -> {
				EventData eventData = new EventData();
				eventData.setDate(event.getDate());
				eventData.setName(event.getName());
				eventData.setLocation(event.getLocation());
				eventData.setDescription(event.getDescription());
				eventData.setNrSeats(event.getNrSeats());
				eventData.setEventId(event.getEventId());
				String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
				eventData.setOrganizer(organizer);
				events.add(eventData);
			});
		} else if (userModel.getRoles().get(0).getRoleName().equalsIgnoreCase("ORGANIZER")) {
			eventModels.stream().filter(ev -> userModel.getUserId() == ev.getUser().getUserId()).forEach(event -> {
				EventData eventData = new EventData();
				eventData.setDate(event.getDate());
				eventData.setName(event.getName());
				eventData.setLocation(event.getLocation());
				eventData.setDescription(event.getDescription());
				eventData.setNrSeats(event.getNrSeats());
				eventData.setEventId(event.getEventId());
				String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
				eventData.setOrganizer(organizer);
				events.add(eventData);
			});
		} else {
			List<Event> eventModelList = new ArrayList<Event>();
			invitationService.readAllByUserEmail(currentUserEmail).forEach(inv->eventModelList.add(inv.getEvent()));
			eventModelList.stream().forEach(event -> {
						EventData eventData = new EventData();
						eventData.setDate(event.getDate());
						eventData.setName(event.getName());
						eventData.setLocation(event.getLocation());
						eventData.setDescription(event.getDescription());
						eventData.setNrSeats(event.getNrSeats());
						eventData.setEventId(event.getEventId());
						String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
						eventData.setOrganizer(organizer);
						events.add(eventData);
					});
		}
		response.setEvents(events);

		response.setStatusCode("200");
		return response;
	}

	@Override
	public EventDataResponse getCompletedEventsForCurrentUser(String currentUserEmail) {
		User userModel = userDao.findByEmail(currentUserEmail);
		EventDataResponse response = new EventDataResponse();
		List<Event> eventModels = eventDao.readAll();
		List<EventData> events = new ArrayList<EventData>();
		if (userModel.getRoles().get(0).getRoleName().equalsIgnoreCase("ADMIN")) {
			eventModels.stream().forEach(event -> {
				EventData eventData = new EventData();
				eventData.setDate(event.getDate());
				eventData.setName(event.getName());
				eventData.setLocation(event.getLocation());
				eventData.setDescription(event.getDescription());
				eventData.setNrSeats(event.getNrSeats());
				eventData.setEventId(event.getEventId());
				String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
				eventData.setOrganizer(organizer);
				events.add(eventData);
			});
		} else if (userModel.getRoles().get(0).getRoleName().equalsIgnoreCase("ORGANIZER")) {
			eventModels.stream().filter(ev -> userModel.getUserId() == ev.getUser().getUserId()).forEach(event -> {
				EventData eventData = new EventData();
				eventData.setDate(event.getDate());
				eventData.setName(event.getName());
				eventData.setLocation(event.getLocation());
				eventData.setDescription(event.getDescription());
				eventData.setNrSeats(event.getNrSeats());
				eventData.setEventId(event.getEventId());
				String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
				eventData.setOrganizer(organizer);
				events.add(eventData);
			});
		} else {
			List<Event> eventModelList = new ArrayList<Event>();
			invitationService.readAllByUserEmail(currentUserEmail).forEach(inv->eventModelList.add(inv.getEvent()));
			eventModelList.stream().forEach(event -> {
						EventData eventData = new EventData();
						eventData.setDate(event.getDate());
						eventData.setName(event.getName());
						eventData.setLocation(event.getLocation());
						eventData.setDescription(event.getDescription());
						eventData.setNrSeats(event.getNrSeats());
						eventData.setEventId(event.getEventId());
						String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
						eventData.setOrganizer(organizer);
						events.add(eventData);
					});
		}
		Date currentDate = new Date(System.currentTimeMillis());
		response.setEvents(events.stream().filter(ev -> ev.getDate().before(currentDate)).collect(Collectors.toList()));
		response.setStatusCode("200");
		return response;
	}

	@Override
	public EventDataResponse getNextEventsForCurrentUser(String currentUserEmail) {
		User userModel = userDao.findByEmail(currentUserEmail);
		EventDataResponse response = new EventDataResponse();
		List<Event> eventModels = eventDao.readAll();
		List<EventData> events = new ArrayList<EventData>();
		if (userModel.getRoles().get(0).getRoleName().equalsIgnoreCase("ADMIN")) {
			eventModels.stream().forEach(event -> {
				EventData eventData = new EventData();
				eventData.setDate(event.getDate());
				eventData.setName(event.getName());
				eventData.setLocation(event.getLocation());
				eventData.setDescription(event.getDescription());
				eventData.setNrSeats(event.getNrSeats());
				eventData.setEventId(event.getEventId());
				String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
				eventData.setOrganizer(organizer);
				events.add(eventData);
			});
		} else if (userModel.getRoles().get(0).getRoleName().equalsIgnoreCase("ORGANIZER")) {
			eventModels.stream().filter(ev -> userModel.getUserId() == ev.getUser().getUserId()).forEach(event -> {
				EventData eventData = new EventData();
				eventData.setDate(event.getDate());
				eventData.setName(event.getName());
				eventData.setLocation(event.getLocation());
				eventData.setDescription(event.getDescription());
				eventData.setNrSeats(event.getNrSeats());
				eventData.setEventId(event.getEventId());
				String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
				eventData.setOrganizer(organizer);
				events.add(eventData);
			});
		} else {
			List<Event> eventModelList = new ArrayList<Event>();
			invitationService.readAllByUserEmail(currentUserEmail).forEach(inv->eventModelList.add(inv.getEvent()));
			eventModelList.stream().forEach(event -> {
						EventData eventData = new EventData();
						eventData.setDate(event.getDate());
						eventData.setName(event.getName());
						eventData.setLocation(event.getLocation());
						eventData.setDescription(event.getDescription());
						eventData.setNrSeats(event.getNrSeats());
						eventData.setEventId(event.getEventId());
						String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
						eventData.setOrganizer(organizer);
						events.add(eventData);
					});
		}
		Date currentDate = new Date(System.currentTimeMillis());
		response.setEvents(events.stream().filter(ev -> ev.getDate().after(currentDate)).collect(Collectors.toList()));

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
		notificationService.createNotification(event.getUser().getEmail(), "Your event was created.");
		return Objects.nonNull(eventDao.createOrUpdate(event));
	}

	@Override
	public boolean editEvent(EditEventRequest request) {
		Event event = eventDao.findById(Integer.valueOf(request.getEventId()));
		event.setName(request.getName());
		event.setDescription(request.getDescription());
		event.setLocation(request.getLocation());
		event.setNrSeats(request.getNrSeats());
		event.setDate(request.getDate());
		notificationService.createNotification(event.getUser().getEmail(),
				"Your event [" + event.getEventId() + "] was edited.");
		return Objects.nonNull(eventDao.createOrUpdate(event));
	}

	@Override
	public SimpleResponse deleteEvent(String eventCode) {
		SimpleResponse simpleResponse = new SimpleResponse();
		try {
			Event event = eventDao.findById(Integer.valueOf(eventCode));
			if (Objects.isNull(event)) {
				simpleResponse.setStatusCode("404");
				simpleResponse.setMessage("Event not found!");
			} else {
				simpleResponse.setStatusCode("200");
				notificationService.createNotification(event.getUser().getEmail(),
						"Your event [" + event.getEventId() + "] was deleted.");
				eventDao.delete(event);
			}
		} catch (Exception e) {
			simpleResponse.setStatusCode("404");
		}

		return simpleResponse;
	}

	@Override
	public EventDataResponse getEventById(String eventId) {
		EventDataResponse response = new EventDataResponse();
		List<EventData> events = new ArrayList<EventData>();
		Event event = eventDao.findById(Integer.valueOf(eventId));
		if (Objects.nonNull(event)) {
			EventData eventData = new EventData();
			eventData.setDate(event.getDate());
			eventData.setName(event.getName());
			eventData.setLocation(event.getLocation());
			eventData.setDescription(event.getDescription());
			eventData.setNrSeats(event.getNrSeats());
			eventData.setEventId(event.getEventId());
			String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
			eventData.setOrganizer(organizer);
			events.add(eventData);

		}
		response.setEvents(events);

		response.setStatusCode("200");
		return response;
	}

	@Override
	public EventDataResponse getEventByInvitationId(String eventId) {
		EventDataResponse response = new EventDataResponse();
		List<EventData> events = new ArrayList<EventData>();
		Invitation invitation = invitationDao.findById(Integer.valueOf(eventId));
		Event event = invitation.getEvent();
		if (Objects.nonNull(event)) {
			EventData eventData = new EventData();
			eventData.setDate(event.getDate());
			eventData.setName(event.getName());
			eventData.setLocation(event.getLocation());
			eventData.setDescription(event.getDescription());
			eventData.setNrSeats(event.getNrSeats());
			eventData.setEventId(event.getEventId());
			String organizer = event.getUser().getFirstName() + " " + event.getUser().getLastName();
			eventData.setOrganizer(organizer);
			events.add(eventData);

		}
		response.setEvents(events);

		response.setStatusCode("200");
		return response;
	}

}
