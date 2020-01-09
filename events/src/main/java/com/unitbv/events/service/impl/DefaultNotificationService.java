package com.unitbv.events.service.impl;

import java.util.Arrays;
import java.util.Objects;

import com.mysql.cj.util.StringUtils;
import com.unitbv.events.dao.NotificationDao;
import com.unitbv.events.dao.UserDao;
import com.unitbv.events.model.Event;
import com.unitbv.events.model.Notification;
import com.unitbv.events.model.User;
import com.unitbv.events.response.SimpleResponse;
import com.unitbv.events.service.NotificationService;
import com.unitbv.events.util.EntityDAOImplFactory;

public class DefaultNotificationService implements NotificationService {
	private static final String PERSISTENCE_UNIT_NAME = "events";
	private EntityDAOImplFactory entityDAOImplFactory;
	private NotificationDao notificationDao;
	private UserDao userDao;

	public DefaultNotificationService() {
		notificationDao = entityDAOImplFactory.createNewNotificationDao(PERSISTENCE_UNIT_NAME);
		userDao = entityDAOImplFactory.createNewUserDao(PERSISTENCE_UNIT_NAME);

	}

	@Override
	public Notification createNotification(String userEmail, String message) {
		if (StringUtils.isEmptyOrWhitespaceOnly(userEmail) || StringUtils.isEmptyOrWhitespaceOnly(message)) {
			return null;
		}
		User user = userDao.findByEmail(userEmail);
		if (Objects.isNull(user)) {
			return null;
		}
		Notification notification = new Notification();
		notification.setDescription(message);
		notification.setUsers(Arrays.asList(user));
		return notificationDao.createOrUpdate(notification);
	}

	@Override
	public SimpleResponse deleteNotification(String notificationCode) {
		SimpleResponse simpleResponse = new SimpleResponse();
		try {
			Notification not = notificationDao.findById(Integer.valueOf(notificationCode));
			if (Objects.isNull(not)) {
				simpleResponse.setStatusCode("404");
				simpleResponse.setMessage("Event not found!");
			} else {
				simpleResponse.setStatusCode("200");
				notificationDao.delete(not);
			}
		} catch (Exception e) {
			simpleResponse.setStatusCode("404");
		}

		return simpleResponse;
	}

	@Override
	public SimpleResponse deleteAllNotification(String userEmail) {
		SimpleResponse simpleResponse = new SimpleResponse();
		User user = new User();
		user.getNotifications().forEach(not -> notificationDao.delete(not));
		simpleResponse.setStatusCode("200");
		return simpleResponse;
	}

}
