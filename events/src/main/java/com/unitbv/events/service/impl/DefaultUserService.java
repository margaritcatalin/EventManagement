package com.unitbv.events.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;
import com.unitbv.events.dao.RoleDao;
import com.unitbv.events.dao.UserDao;
import com.unitbv.events.data.EventData;
import com.unitbv.events.data.RoleData;
import com.unitbv.events.data.UserData;
import com.unitbv.events.model.Role;
import com.unitbv.events.model.User;
import com.unitbv.events.request.RegisterRequest;
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
		User user =new User();
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setIsActive(true);
		user.setIsAvailable(true);
		Role role=roleDao.findByRoleName("CUSTOMER");
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
	public UserDataResponse getUserByEmail(String email) {
		UserDataResponse userDataResponse=new UserDataResponse();
		User userModel=userDao.findByEmail(email);
		if(Objects.isNull(userModel)) {
			userDataResponse.setStatusCode("404");
		}else {
			userDataResponse.setStatusCode("200");
			UserData userData=new UserData();
			
			userData.setEmail(userModel.getEmail());
			userData.setFirstName(userModel.getFirstName());
			userData.setLastName(userModel.getLastName());
			List<RoleData> userRoles=new ArrayList();
			userModel.getRoles().stream().forEach(role->{
				RoleData roleData=new RoleData();
				roleData.setRoleName(role.getRoleName());
				userRoles.add(roleData);
			});
			userData.setRoles(userRoles);
			List<EventData> events=new ArrayList();
			userModel.getEvents().stream().forEach(event->{
				EventData eventData= new EventData();
				eventData.setDate(event.getDate());
				eventData.setName(event.getName());
				eventData.setLocation(event.getLocation());
				eventData.setDescription(event.getDescription());
				eventData.setNrSeats(event.getNrSeats());
				String organizer= event.getUser().getFirstName()+ " "+ event.getUser().getLastName();
				eventData.setOrganizer(organizer);
				events.add(eventData);
			});
			userData.setEvents(events);
			userDataResponse.setUserData(userData);
		}
		return userDataResponse;
	}
}
