package com.unitbv.events.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
import com.unitbv.events.model.Role;
import com.unitbv.events.model.User;
import com.unitbv.events.request.AvailabilityRequest;
import com.unitbv.events.request.EditProfileRequest;
import com.unitbv.events.request.RegisterRequest;
import com.unitbv.events.response.CustomerDataResponse;
import com.unitbv.events.response.InvitationDataResponse;
import com.unitbv.events.response.NotificationDataResponse;
import com.unitbv.events.response.UserDataResponse;
import com.unitbv.events.service.UserService;
import com.unitbv.events.util.EntityDAOImplFactory;

public class DefaultUserService implements UserService {

	private static final String PERSISTENCE_UNIT_NAME = "events";
	private EntityDAOImplFactory entityDAOImplFactory;
	private UserDao userDao;
	private RoleDao roleDao;

	public DefaultUserService() {
		userDao = entityDAOImplFactory.createNewUserDao(PERSISTENCE_UNIT_NAME);
		roleDao = entityDAOImplFactory.createNewRoleDao(PERSISTENCE_UNIT_NAME);

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
		return Objects.nonNull(userDao.createOrUpdate(userModel));
	}

	@Override
	public boolean updateUserAvailability(AvailabilityRequest request) {
		User userModel = userDao.findByEmail(request.getCurrentEmail());
		userModel.setIsAvailable(request.getAvailable());
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
			userModel.getNotifications().stream().forEach(not -> {
				NotificationData notificationData = new NotificationData();
				notificationData.setDescription(not.getDescription());
				notifications.add(notificationData);
			});
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
			userModel.getInvitations().stream().forEach(inv -> {
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
		return Objects.nonNull(userDao.createOrUpdate(userModel));
	}

}
