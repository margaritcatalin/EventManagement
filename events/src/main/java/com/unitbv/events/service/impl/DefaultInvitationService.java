package com.unitbv.events.service.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.unitbv.events.dao.EventDao;
import com.unitbv.events.dao.InvitationDao;
import com.unitbv.events.dao.UserDao;
import com.unitbv.events.data.InvitationData;
import com.unitbv.events.data.InvitationFileData;
import com.unitbv.events.model.Event;
import com.unitbv.events.model.Invitation;
import com.unitbv.events.model.InvitationFile;
import com.unitbv.events.model.User;
import com.unitbv.events.request.AcceptInvitationRequest;
import com.unitbv.events.request.SendInvitationRequest;
import com.unitbv.events.response.InvitationDataResponse;
import com.unitbv.events.response.SimpleResponse;
import com.unitbv.events.service.InvitationService;
import com.unitbv.events.service.NotificationService;
import com.unitbv.events.util.EntityDAOImplFactory;

public class DefaultInvitationService implements InvitationService {
	private static final String PERSISTENCE_UNIT_NAME = "events";
	private EntityDAOImplFactory entityDAOImplFactory;
	private InvitationDao invitationDao;
	private UserDao userDao;
	private EventDao eventDao;
	private NotificationService notificationService;

	public DefaultInvitationService() {
		invitationDao = entityDAOImplFactory.createNewInvitationDao(PERSISTENCE_UNIT_NAME);
		userDao = entityDAOImplFactory.createNewUserDao(PERSISTENCE_UNIT_NAME);
		eventDao = entityDAOImplFactory.createNewEventDao(PERSISTENCE_UNIT_NAME);
		notificationService = new DefaultNotificationService();
	}

	@Override
	public InvitationDataResponse getInvitationByCode(String invitationCode) {
		InvitationDataResponse invitationDataResponse = new InvitationDataResponse();
		Invitation inv = invitationDao.findById(Integer.valueOf(invitationCode));
		if (Objects.isNull(inv)) {
			invitationDataResponse.setStatusCode("404");
		} else {
			invitationDataResponse.setStatusCode("200");
			List<InvitationData> invitations = new ArrayList<>();
			InvitationData invitationData = new InvitationData();
			invitationData.setCreationDate(inv.getCreationDate());
			invitationData.setDescription(inv.getDescription());
			invitationData.setEventDate(inv.getEvent().getDate());
			invitationData.setEventName(inv.getEvent().getName());
			if (Objects.isNull(inv.getIsAccepted())) {
				invitationData.setIsAccepted("Wait");
			} else {
				invitationData.setIsAccepted(inv.getIsAccepted() ? "Yes" : "No");
			}
			invitationData.setReference("Ref: " + inv.getInvitationId());
			InvitationFileData invitationFile = new InvitationFileData();
			invitationFile.setExtension(inv.getInvitationfile().getExtension());
			invitationFile.setFileData(inv.getInvitationfile().getFileData());
			invitationData.setInvitationFile(invitationFile);
			invitations.add(invitationData);
			invitationDataResponse.setInvitations(invitations);
		}
		return invitationDataResponse;
	}

	@Override
	public List<Invitation> readAllByUserEmail(String email) {
		User user = userDao.findByEmail(email);
		return invitationDao.findByUser(user);
	}

	@Override
	public InvitationDataResponse getAllInvitationForUser(String currentUserEmail) {
		InvitationDataResponse invitationDataResponse = new InvitationDataResponse();
		User userModel = userDao.findByEmail(currentUserEmail);
		if (Objects.isNull(userModel)) {
			invitationDataResponse.setStatusCode("404");
		} else {
			invitationDataResponse.setStatusCode("200");
			List<InvitationData> invitations = new ArrayList<>();
			readAllByUserEmail(currentUserEmail).stream().forEach(inv -> {
				InvitationData invitationData = new InvitationData();
				invitationData.setCreationDate(inv.getCreationDate());
				invitationData.setDescription(inv.getDescription());
				invitationData.setEventDate(inv.getEvent().getDate());
				invitationData.setEventName(inv.getEvent().getName());
				if (Objects.isNull(inv.getIsAccepted())) {
					invitationData.setIsAccepted("Wait");
				} else {
					invitationData.setIsAccepted(inv.getIsAccepted() ? "Yes" : "No");
				}
				invitationData.setReference("Ref: " + inv.getInvitationId());
				InvitationFileData invitationFile = new InvitationFileData();
				invitationFile.setExtension(inv.getInvitationfile().getExtension());
				invitationFile.setFileData(inv.getInvitationfile().getFileData());
				invitationData.setInvitationFile(invitationFile);
				invitations.add(invitationData);
			});
			invitationDataResponse.setInvitations(invitations);
		}
		return invitationDataResponse;
	}

	@Override
	public boolean sendInvitation(SendInvitationRequest sendInvitationRequest) {
		if ("All".equalsIgnoreCase(sendInvitationRequest.getUserEmail())) {
			List<User> users = userDao.readAll().stream().filter(user -> user.getIsActive() && user.getIsAvailable())
					.collect(Collectors.toList());
			users.forEach(user -> createInvitation(sendInvitationRequest.getInvitationMessage(),
					sendInvitationRequest.getEventCode(), user.getEmail()));
			Event iEvent = eventDao.findById(Integer.valueOf(sendInvitationRequest.getEventCode()));
			notificationService.createNotification(iEvent.getUser().getEmail(),
					"Your invitation for [" + sendInvitationRequest.getEventCode() + "] was sent.");
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
		Event event = eventDao.findById(Integer.valueOf(eventCode));
		User user = userDao.findByEmail(email);
		if (invitationDao.findByEventAndUser(user, event).size() == 0) {
			Invitation invitation = new Invitation();
			invitation.setDescription(message);
			invitation.setEvent(event);
			invitation.setCreationDate(new Date(System.currentTimeMillis()));
			invitation.setIsAccepted(false);
			invitation.setUser(user);
			List<Invitation> inv = invitationDao.findByEventAndUser(user, event);
			if (inv.size() > 0) {
				invitation.setInvitationfile(inv.get(0).getInvitationfile());
			} else {
				InvitationFile invitationFile = new InvitationFile();
				invitationFile.setExtension("txt");
				invitationFile.setFileData(
						createInvitationKey(email, event.getName(), eventCode).getBytes(Charset.forName("UTF-8")));
				invitation.setInvitationfile(invitationFile);
			}
			notificationService.createNotification(event.getUser().getEmail(), "Your invitation for [" + event.getName()
					+ "(" + event.getEventId() + ")] was sent to " + user.getEmail() + ".");
			notificationService.createNotification(user.getEmail(), "Your get a invitation for [" + event.getName()
					+ "(" + event.getEventId() + ")] from " + event.getUser().getEmail() + ".");
			invitationDao.createOrUpdate(invitation);
		} else {
			notificationService.createNotification(user.getEmail(), "Your get a invitation for [" + event.getName()
					+ "(" + event.getEventId() + ")] from " + event.getUser().getEmail() + ".");
		}
	}

	private String createInvitationKey(String userEmail, String eventName, String eventCode) {
		String stringToHash = userEmail + "-" + eventName + "-" + eventCode;
		HashFunction md5 = Hashing.md5();
		HashCode hashString = md5.hashString(stringToHash, Charset.defaultCharset());
		return hashString.toString();
	}

	@Override
	public SimpleResponse acceptInvitation(AcceptInvitationRequest request) {
		SimpleResponse simpleResponse = new SimpleResponse();
		User userModel = userDao.findByEmail(request.getUserEmail());
		if (Objects.isNull(userModel)) {
			simpleResponse.setStatusCode("500");
			simpleResponse.setMessage("User not found!");
		} else {
			Date currentDate = new Date(System.currentTimeMillis());
			userModel.getInvitations().forEach(inv -> {
				if (request.getInvitationCode()
						.equals(Base64.getEncoder().encodeToString(inv.getInvitationfile().getFileData()))) {
					if (inv.getIsAccepted()) {
						simpleResponse.setStatusCode("404");
						simpleResponse.setMessage("You already accepted it.");
					} else {
						if (inv.getEvent().getDate().after(currentDate)) {
							if (inv.getEvent().getNrSeats() > 0) {
								inv.setIsAccepted(true);
								simpleResponse.setStatusCode("200");
								simpleResponse.setMessage("Done!");
								notificationService.createNotification(inv.getEvent().getUser().getEmail(),
										"Your invitation for [" + inv.getEvent().getName() + "("
												+ inv.getEvent().getEventId() + ")] was accepted by"
												+ userModel.getEmail() + ".");
								notificationService.createNotification(userModel.getEmail(),
										"Your ticket was accepted. Event start at [" + inv.getEvent().getDate() + ".");
								invitationDao.createOrUpdate(inv);
								inv.getEvent().setNrSeats(inv.getEvent().getNrSeats() - 1);
								eventDao.createOrUpdate(inv.getEvent());
							} else {
								simpleResponse.setStatusCode("404");
								simpleResponse.setMessage("There are no more places available.");
							}
						} else {
							simpleResponse.setStatusCode("404");
							simpleResponse.setMessage("This event is no longer available.");
						}
					}
				} else {
					simpleResponse.setStatusCode("404");
					simpleResponse.setMessage("Your event key is not correct.");
				}
			});
		}
		return simpleResponse;
	}
}
