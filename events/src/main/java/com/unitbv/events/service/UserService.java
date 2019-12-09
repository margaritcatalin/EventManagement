package com.unitbv.events.service;

import com.unitbv.events.request.RegisterRequest;
import com.unitbv.events.response.UserDataResponse;

public interface UserService {

	boolean createUser(RegisterRequest request);

	boolean checkIfAccountExist(String userName);

	boolean checkIfCredentialsIsCorrect(String userName, String password);
	
	UserDataResponse getUserByEmail(String email);
}
