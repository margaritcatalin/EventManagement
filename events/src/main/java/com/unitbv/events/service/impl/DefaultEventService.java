package com.unitbv.events.service.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.unitbv.events.dao.EventDao;
import com.unitbv.events.dao.InvitationDao;
import com.unitbv.events.dao.InvitationFileDao;
import com.unitbv.events.dao.RoleDao;
import com.unitbv.events.dao.UserDao;
import com.unitbv.events.data.EventData;
import com.unitbv.events.model.Event;
import com.unitbv.events.model.Invitation;
import com.unitbv.events.model.InvitationFile;
import com.unitbv.events.model.Role;
import com.unitbv.events.model.User;
import com.unitbv.events.request.CreateEventRequest;
import com.unitbv.events.request.EditEventRequest;
import com.unitbv.events.request.SendInvitationRequest;
import com.unitbv.events.response.EventDataResponse;
import com.unitbv.events.response.SimpleResponse;
import com.unitbv.events.service.EventService;
import com.unitbv.events.util.EntityDAOImplFactory;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class DefaultEventService implements EventService {
	private static final String PERSISTENCE_UNIT_NAME = "events";
	private EntityDAOImplFactory entityDAOImplFactory;
	private UserDao userDao;
	private EventDao eventDao;
	private RoleDao roleDao;
	private InvitationDao invitationDao;
	private InvitationFileDao invitationFileDao;

	public DefaultEventService() {
		userDao = entityDAOImplFactory.createNewUserDao(PERSISTENCE_UNIT_NAME);
		eventDao = entityDAOImplFactory.createNewEventDao(PERSISTENCE_UNIT_NAME);
		roleDao = entityDAOImplFactory.createNewRoleDao(PERSISTENCE_UNIT_NAME);
		invitationDao = entityDAOImplFactory.createNewInvitationDao(PERSISTENCE_UNIT_NAME);
		invitationFileDao = entityDAOImplFactory.createNewInvitationFileDao(PERSISTENCE_UNIT_NAME);
	}

	@Override
	public EventDataResponse getAllEvents(String currentUserEmail) {
		User userModel = userDao.findByEmail(currentUserEmail);
		EventDataResponse response = new EventDataResponse();
		List<Event> eventModels = eventDao.readAll();
		List<EventData> events = new ArrayList<EventData>();
		Role role = roleDao.findByRoleName("ADMIN");
		if (userModel.getRoles().contains(role)) {
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
		} else {
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
		Role role = roleDao.findByRoleName("ADMIN");
		if (userModel.getRoles().contains(role)) {
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
		} else {
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
		Role role = roleDao.findByRoleName("ADMIN");
		if (userModel.getRoles().contains(role)) {
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
		} else {
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
	public boolean sendInvitation(SendInvitationRequest sendInvitationRequest) {
		if ("All".equalsIgnoreCase(sendInvitationRequest.getUserEmail())) {
			List<User> users = userDao.readAll().stream().filter(user -> user.getIsActive() && user.getIsAvailable())
					.collect(Collectors.toList());
			users.forEach(user -> createInvitation(sendInvitationRequest.getInvitationMessage(),
					sendInvitationRequest.getEventCode(), user.getEmail()));
			return true;
		} else if ("NONE".equalsIgnoreCase(sendInvitationRequest.getUserEmail())) {
			return false;
		} else {
			createInvitation(sendInvitationRequest.getInvitationMessage(), sendInvitationRequest.getEventCode(),
					sendInvitationRequest.getUserEmail());
			return true;
		}
	}

	private void createInvitation(String message, String eventCode, String email) {
		Invitation invitation = new Invitation();
		invitation.setDescription(message);
		Event event = eventDao.findById(Integer.valueOf(eventCode));
		invitation.setEvent(event);
		invitation.setCreationDate(new Date(System.currentTimeMillis()));
		invitation.setIsAccepted(false);
		User user = userDao.findByEmail(email);
		invitation.setUser(user);
		List<Invitation> inv = invitationDao.findByEventAndUser(user, event);
		if (inv.size()>0) {
			invitation.setInvitationfile(inv.get(0).getInvitationfile());
		} else {
			InvitationFile invitationFile = new InvitationFile();
			invitationFile.setExtension("txt");
			invitationFile.setFileData(
					createInvitationKey(email, event.getName(), eventCode).getBytes(Charset.forName("UTF-8")));
			invitation.setInvitationfile(invitationFile);
		}
		invitationDao.createOrUpdate(invitation);
	}

	private String createInvitationKey(String userEmail, String eventName, String eventCode) {
		String stringToHash = userEmail + "-" + eventName + "-" + eventCode;
		HashFunction md5 = Hashing.md5();
		HashCode hashString = md5.hashString(stringToHash, Charset.defaultCharset());
		return hashString.toString();
	}

}
