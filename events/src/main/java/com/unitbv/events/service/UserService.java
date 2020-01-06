package com.unitbv.events.service;

import com.unitbv.events.request.AvailabilityRequest;
import com.unitbv.events.request.EditProfileRequest;
import com.unitbv.events.request.RegisterRequest;
import com.unitbv.events.response.NotificationDataResponse;
import com.unitbv.events.response.UserDataResponse;

public interface UserService {

	boolean createUser(RegisterRequest request);
	
	boolean updateUserProfile(EditProfileRequest request);

	boolean updateUserAvailability(AvailabilityRequest request);
	
	boolean checkIfAccountExist(String userName);

	boolean checkIfCredentialsIsCorrect(String userName, String password);
	
	NotificationDataResponse getAllNotificationForUser(String currentUserEmail);
	
	UserDataResponse getUserByEmail(String email);
}
