package com.unitbv.events.service.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mysql.cj.util.StringUtils;
import com.unitbv.events.dao.RoleDao;
import com.unitbv.events.dao.UserDao;
import com.unitbv.events.data.CustomerData;
import com.unitbv.events.data.EventData;
import com.unitbv.events.data.InvitationData;
import com.unitbv.events.data.InvitationFileData;
import com.unitbv.events.data.NotificationData;
import com.unitbv.events.data.RoleData;
import com.unitbv.events.data.UserData;
import com.unitbv.events.model.Notification;
import com.unitbv.events.model.Role;
import com.unitbv.events.model.User;
import com.unitbv.events.request.AcceptInvitationRequest;
import com.unitbv.events.request.AvailabilityRequest;
import com.unitbv.events.request.EditProfileRequest;
import com.unitbv.events.request.RegisterRequest;
import com.unitbv.events.response.CustomerDataResponse;
import com.unitbv.events.response.InvitationDataResponse;
import com.unitbv.events.response.NotificationDataResponse;
import com.unitbv.events.response.SimpleResponse;
import com.unitbv.events.response.UserDataResponse;
import com.unitbv.events.service.InvitationService;
import com.unitbv.events.service.NotificationService;
import com.unitbv.events.service.UserService;
import com.unitbv.events.util.EntityDAOImplFactory;

public class DefaultUserService implements UserService {

	private static final String PERSISTENCE_UNIT_NAME = "events";
	private EntityDAOImplFactory entityDAOImplFactory;
	private UserDao userDao;
	private RoleDao roleDao;
	private NotificationService notificationService;
	private InvitationService invitationService;

	public DefaultUserService() {
		userDao = entityDAOImplFactory.createNewUserDao(PERSISTENCE_UNIT_NAME);
		roleDao = entityDAOImplFactory.createNewRoleDao(PERSISTENCE_UNIT_NAME);
		notificationService = new DefaultNotificationService();
		invitationService = new DefaultInvitationService();

	}

	@Override
	public boolean createUser(RegisterRequest request) {
		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setIsActive(true);
		user.setIsAvailable(true);
		Role role = roleDao.findByRoleName("CUSTOMER");
		user.setRoles(Arrays.asList(role));
		return Objects.nonNull(userDao.createOrUpdate(user));
	}

	@Override
	public boolean checkIfAccountExist(String userName) {
		if (Objects.isNull(userName)) {
			return false;
		}
		User user = userDao.findByEmail(userName);
		if (Objects.nonNull(user)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkIfCredentialsIsCorrect(String userName, String password) {
		if (Objects.isNull(userName) || Objects.isNull(password)) {
			return false;
		}
		User user = userDao.findByEmail(userName);
		if (Objects.nonNull(user) && password.equals(user.getPassword())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean checkUserStatus(String userName) {
		if (Objects.isNull(userName)) {
			return false;
		}
		User user = userDao.findByEmail(userName);
		if (Objects.nonNull(user)) {
			return user.getIsActive();
		}
		return false;
	}

	@Override
	public UserDataResponse getUserByEmail(String email) {
		UserDataResponse userDataResponse = new UserDataResponse();
		User userModel = userDao.findByEmail(email);
		if (Objects.isNull(userModel)) {
			userDataResponse.setStatusCode("404");
		} else {
			userDataResponse.setStatusCode("200");
			UserData userData = new UserData();

			userData.setEmail(userModel.getEmail());
			userData.setFirstName(userModel.getFirstName());
			userData.setLastName(userModel.getLastName());
			userData.setAvailability(userModel.getIsAvailable());
			List<RoleData> userRoles = new ArrayList<>();
			userModel.getRoles().stream().forEach(role -> {
				RoleData roleData = new RoleData();
				roleData.setRoleName(role.getRoleName());
				userRoles.add(roleData);
			});
			userData.setRoles(userRoles);
			List<EventData> events = new ArrayList<>();
			userModel.getEvents().stream().forEach(event -> {
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
			userData.setEvents(events);
			userDataResponse.setUserData(userData);
		}
		return userDataResponse;
	}

	@Override
	public boolean updateUserProfile(EditProfileRequest request) {
		User userModel = userDao.findByEmail(request.getCurrentEmail());
		if (!userModel.getEmail().equalsIgnoreCase(request.getEmail())) {
			userModel.setEmail(request.getEmail());
		}
		if (!StringUtils.isEmptyOrWhitespaceOnly(request.getNewPassword())) {
			userModel.setPassword(request.getNewPassword());
		}
		userModel.setFirstName(request.getFirstName());
		userModel.setLastName(request.getLastName());
		notificationService.createNotification(userModel.getEmail(), "Your profile was updated.");
		return Objects.nonNull(userDao.createOrUpdate(userModel));
	}

	@Override
	public boolean updateUserAvailability(AvailabilityRequest request) {
		User userModel = userDao.findByEmail(request.getCurrentEmail());
		userModel.setIsAvailable(request.getAvailable());
		notificationService.createNotification(userModel.getEmail(), "Your availability was changed.");
		return Objects.nonNull(userDao.createOrUpdate(userModel));
	}

	@Override
	public NotificationDataResponse getAllNotificationForUser(String currentUserEmail) {
		NotificationDataResponse notificationDataResponse = new NotificationDataResponse();
		User userModel = userDao.findByEmail(currentUserEmail);
		if (Objects.isNull(userModel)) {
			notificationDataResponse.setStatusCode("404");
		} else {
			notificationDataResponse.setStatusCode("200");
			List<NotificationData> notifications = new ArrayList<>();
			try {
				notificationService.readAllByUserEmail(currentUserEmail).forEach(not -> {
					NotificationData notificationData = new NotificationData();
					notificationData.setDescription(not.getDescription());
					notificationData.setNotificationId(String.valueOf(not.getNotificationId()));
					notifications.add(notificationData);
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			notificationDataResponse.setNotifications(notifications);
		}
		return notificationDataResponse;

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
			invitationService.readAllByUserEmail(currentUserEmail).stream().forEach(inv -> {
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
	public CustomerDataResponse getAllUsers() {
		CustomerDataResponse customerDataResponse = new CustomerDataResponse();
		List<User> users = userDao.readAll();
		if (Objects.isNull(users)) {
			customerDataResponse.setStatusCode("404");
		} else {
			customerDataResponse.setStatusCode("200");
			List<CustomerData> customers = new ArrayList<>();
			users.stream().forEach(usr -> {
				CustomerData customerData = new CustomerData();
				customerData.setFirstName(usr.getFirstName());
				customerData.setLastName(usr.getLastName());
				customerData.setEmail(usr.getEmail());
				customerData.setUserStatus(usr.getIsActive() ? "Yes" : "No");
				customerData.setAvailabilityStatus(usr.getIsAvailable() ? "Yes" : "No");
				customers.add(customerData);
			});
			customerDataResponse.setCustomers(customers);
		}
		return customerDataResponse;
	}

	@Override
	public CustomerDataResponse getAllCustomers() {
		CustomerDataResponse customerDataResponse = new CustomerDataResponse();
		Role role = roleDao.findByRoleName("CUSTOMER");
		List<User> users = userDao.readAll().stream()
				.filter(customer -> customer.getRoles().get(0).getRoleId() == role.getRoleId())
				.collect(Collectors.toList());
		if (Objects.isNull(users)) {
			customerDataResponse.setStatusCode("404");
		} else {
			customerDataResponse.setStatusCode("200");
			List<CustomerData> customers = new ArrayList<>();
			users.stream().forEach(usr -> {
				CustomerData customerData = new CustomerData();
				customerData.setFirstName(usr.getFirstName());
				customerData.setLastName(usr.getLastName());
				customerData.setEmail(usr.getEmail());
				customerData.setUserStatus(usr.getIsActive() ? "Yes" : "No");
				customerData.setAvailabilityStatus(usr.getIsAvailable() ? "Yes" : "No");
				customers.add(customerData);
			});
			customerDataResponse.setCustomers(customers);
		}
		return customerDataResponse;
	}

	@Override
	public CustomerDataResponse getAllOrganizers() {
		CustomerDataResponse customerDataResponse = new CustomerDataResponse();
		Role role = roleDao.findByRoleName("ORGANIZER");
		List<User> users = userDao.readAll().stream()
				.filter(customer -> customer.getRoles().get(0).getRoleId() == role.getRoleId())
				.collect(Collectors.toList());
		if (Objects.isNull(users)) {
			customerDataResponse.setStatusCode("404");
		} else {
			customerDataResponse.setStatusCode("200");
			List<CustomerData> customers = new ArrayList<>();
			users.stream().forEach(usr -> {
				CustomerData customerData = new CustomerData();
				customerData.setFirstName(usr.getFirstName());
				customerData.setLastName(usr.getLastName());
				customerData.setEmail(usr.getEmail());
				customerData.setUserStatus(usr.getIsActive() ? "Yes" : "No");
				customerData.setAvailabilityStatus(usr.getIsAvailable() ? "Yes" : "No");
				customers.add(customerData);
			});
			customerDataResponse.setCustomers(customers);
		}
		return customerDataResponse;
	}

	@Override
	public boolean deactivateUser(String customerEmail) {
		User userModel = userDao.findByEmail(customerEmail);
		if (userModel.getIsActive()) {
			userModel.setIsActive(false);
		} else {
			userModel.setIsActive(true);
		}
		notificationService.createNotification(userModel.getEmail(), "Your account was deactivated.");
		return Objects.nonNull(userDao.createOrUpdate(userModel));
	}

	@Override
	public void forgotPassword(String customerEmail) {
		User user = userDao.findByEmail(customerEmail);
		final String username = "licentarentcar@gmail.com";
		final String password = "catamarg1234";
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "true"); // TLS
		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("licentarentcar@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(customerEmail));
			message.setSubject("Forgot password");
			message.setText("Dear " + user.getFirstName() + " " + user.getLastName() + ","
					+ "\n\n Please use this code: "
					+ createForgotPasswordKey(user.getEmail(), user.getPassword(), String.valueOf(user.getUserId())));

			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SimpleResponse enterForgotPasswordCode(AcceptInvitationRequest request) {
		SimpleResponse simpleResponse = new SimpleResponse();
		User user = userDao.findByEmail(request.getUserEmail());
		if (Objects.isNull(user)) {
			simpleResponse.setStatusCode("500");
			simpleResponse.setMessage("User not found!");
		} else if (request.getInvitationCode().equals(
				createForgotPasswordKey(user.getEmail(), user.getPassword(), String.valueOf(user.getUserId())))) {
			simpleResponse.setStatusCode("200");
			simpleResponse.setMessage("Done!");
		}
		return simpleResponse;
	}

	@Override
	public SimpleResponse changePassword(AcceptInvitationRequest request) {
		SimpleResponse simpleResponse = new SimpleResponse();
		User user = userDao.findByEmail(request.getUserEmail());
		if (Objects.isNull(user)) {
			simpleResponse.setStatusCode("500");
			simpleResponse.setMessage("User not found!");
		} else if (!StringUtils.isEmptyOrWhitespaceOnly(request.getInvitationCode())) {
			user.setPassword(request.getInvitationCode());
			userDao.createOrUpdate(user);
			notificationService.createNotification(user.getEmail(), "Your profile was updated.");
			simpleResponse.setStatusCode("200");
			simpleResponse.setMessage("Done!");
		} else {
			simpleResponse.setStatusCode("404");
			simpleResponse.setMessage("Your password is invalid!");
		}
		return simpleResponse;
	}

	private String createForgotPasswordKey(String userEmail, String lastPassword, String userCode) {
		String stringToHash = userEmail + "-" + lastPassword + "-" + userCode;
		HashFunction md5 = Hashing.md5();
		HashCode hashString = md5.hashString(stringToHash, Charset.defaultCharset());
		return hashString.toString();
	}

}
