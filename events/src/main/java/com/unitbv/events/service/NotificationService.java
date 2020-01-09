package com.unitbv.events.service;

import java.util.List;

import com.unitbv.events.model.Notification;
import com.unitbv.events.response.SimpleResponse;

public interface NotificationService {
	Notification createNotification(String userEmail, String message);

	SimpleResponse deleteNotification(String notificationCode);

	SimpleResponse deleteAllNotification(String userEmail);

	List<Notification> readAllByUserEmail(String email);
}
