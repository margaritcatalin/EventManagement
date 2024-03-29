package com.unitbv.events.service;

import com.unitbv.events.request.AcceptInvitationRequest;
import com.unitbv.events.request.AvailabilityRequest;
import com.unitbv.events.request.EditProfileRequest;
import com.unitbv.events.request.RegisterRequest;
import com.unitbv.events.response.CustomerDataResponse;
import com.unitbv.events.response.SimpleResponse;
import com.unitbv.events.response.UserDataResponse;

public interface UserService {

	boolean createUser(RegisterRequest request);

	boolean updateUserProfile(EditProfileRequest request);

	boolean updateUserAvailability(AvailabilityRequest request);

	boolean checkUserStatus(String userName);

	boolean deactivateUser(String customerEmail);

	boolean checkIfAccountExist(String userName);

	boolean checkIfCredentialsIsCorrect(String userName, String password);

	UserDataResponse getUserByEmail(String email);

	CustomerDataResponse getAllUsers();

	CustomerDataResponse getAllCustomers();

	CustomerDataResponse getAllOrganizers();

	void forgotPassword(String customerEmail);

	SimpleResponse enterForgotPasswordCode(AcceptInvitationRequest request);

	SimpleResponse changePassword(AcceptInvitationRequest request);
}
