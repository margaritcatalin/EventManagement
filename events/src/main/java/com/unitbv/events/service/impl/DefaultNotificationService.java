package com.unitbv.events.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.mysql.cj.util.StringUtils;
import com.unitbv.events.dao.NotificationDao;
import com.unitbv.events.dao.UserDao;
import com.unitbv.events.data.NotificationData;
import com.unitbv.events.model.Notification;
import com.unitbv.events.model.User;
import com.unitbv.events.response.NotificationDataResponse;
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
		notification.setUser(user);
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
		while (readAllByUserEmail(userEmail).size() > 0) {
			notificationDao.delete(readAllByUserEmail(userEmail).get(0));
		}
		simpleResponse.setStatusCode("200");
		return simpleResponse;
	}

	@Override
	public List<Notification> readAllByUserEmail(String email) {
		User user = userDao.findByEmail(email);
		return notificationDao.findByUser(user);
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
					readAllByUserEmail(currentUserEmail).forEach(not -> {
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
}
