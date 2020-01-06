package com.unitbv.events.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;

import com.google.gson.Gson;
import com.unitbv.events.request.AvailabilityRequest;
import com.unitbv.events.request.CreateEventRequest;
import com.unitbv.events.request.EditProfileRequest;
import com.unitbv.events.request.GetUserRequest;
import com.unitbv.events.request.LoginRequst;
import com.unitbv.events.request.RegisterRequest;
import com.unitbv.events.response.SimpleResponse;
import com.unitbv.events.service.EventService;
import com.unitbv.events.service.UserService;
import com.unitbv.events.service.impl.DefaultEventService;
import com.unitbv.events.service.impl.DefaultUserService;

public class ServerUtilities implements Runnable {
	private ServerSocket serverSocket;
	private Gson gson;
	private UserService userService;
	private EventService eventService;

	public ServerUtilities(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		gson = new Gson();
		userService = new DefaultUserService();
		eventService = new DefaultEventService();

	}

	public void accept() throws IOException {
		while (!Thread.interrupted()) {
			try (Socket socket = serverSocket.accept()) {
				BufferedWriter bufferedOutputWriter = new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream()));
				BufferedReader bufferedInputReader = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), "UTF-8"));

				String inputCommand = bufferedInputReader.readLine();
				String receivedData = bufferedInputReader.readLine();
				if (Objects.isNull(receivedData)) {
					SimpleResponse response = new SimpleResponse();
					response.setStatusCode("404");
					response.setMessage("Command not Found");
					bufferedOutputWriter.write(gson.toJson(response));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				}
				if ("login".equalsIgnoreCase(inputCommand)) {
					LoginRequst request = gson.fromJson(receivedData, LoginRequst.class);
					SimpleResponse response = new SimpleResponse();
					if (!userService.checkIfCredentialsIsCorrect(request.getEmail(), request.getPassword())) {
						response.setStatusCode("500");
						response.setMessage("Email or password incorect!");
					} else {
						response.setStatusCode("200");
						response.setMessage("Login successful!");
					}
					bufferedOutputWriter.write(gson.toJson(response));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("register".equalsIgnoreCase(inputCommand)) {
					SimpleResponse response = new SimpleResponse();
					RegisterRequest registerRequest = gson.fromJson(receivedData, RegisterRequest.class);
					if (userService.checkIfAccountExist(registerRequest.getEmail())) {
						response.setStatusCode("301");
						response.setMessage("Email is already used!");
					} else if (userService.createUser(registerRequest)) {
						response.setStatusCode("200");
						response.setMessage("Successful!");
					} else {
						response.setStatusCode("500");
						response.setMessage("Internal Server Errror!");
					}
					bufferedOutputWriter.write(gson.toJson(response));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getUser".equalsIgnoreCase(inputCommand)) {
					GetUserRequest getUserRequest = gson.fromJson(receivedData, GetUserRequest.class);
					bufferedOutputWriter.write(gson.toJson(userService.getUserByEmail(getUserRequest.getEmail())));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("editProfile".equalsIgnoreCase(inputCommand)) {
					SimpleResponse response = new SimpleResponse();
					EditProfileRequest updateUserRequest = gson.fromJson(receivedData, EditProfileRequest.class);
					if (userService.checkIfAccountExist(updateUserRequest.getEmail())
							&& !updateUserRequest.getEmail().equalsIgnoreCase(updateUserRequest.getCurrentEmail())) {
						response.setStatusCode("301");
						response.setMessage("Email is already used!");
					} else if (userService.updateUserProfile(updateUserRequest)) {
						response.setStatusCode("200");
						response.setMessage("Successful!");
					} else {
						response.setStatusCode("500");
						response.setMessage("Internal Server Errror!");
					}
					bufferedOutputWriter.write(gson.toJson(response));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("setUserAvailability".equalsIgnoreCase(inputCommand)) {
					SimpleResponse response = new SimpleResponse();
					AvailabilityRequest availabilityRequest = gson.fromJson(receivedData, AvailabilityRequest.class);
					if (userService.checkIfAccountExist(availabilityRequest.getCurrentEmail())
							&& userService.updateUserAvailability(availabilityRequest)) {
						response.setStatusCode("200");
						response.setMessage("Successful!");
					} else {
						response.setStatusCode("500");
						response.setMessage("Internal Server Errror!");
					}
					bufferedOutputWriter.write(gson.toJson(response));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getAllEvents".equalsIgnoreCase(inputCommand)) {
					bufferedOutputWriter.write(gson.toJson(eventService.getAllEvents()));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getNotificationForCurrentUser".equalsIgnoreCase(inputCommand)) {
					String currentUserEmail=receivedData;
					bufferedOutputWriter.write(gson.toJson(userService.getAllNotificationForUser(currentUserEmail)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("createEvent".equalsIgnoreCase(inputCommand)) {
					SimpleResponse response = new SimpleResponse();
					CreateEventRequest createEventRequest = gson.fromJson(receivedData, CreateEventRequest.class);
					if (eventService.createEvent(createEventRequest)) {
						response.setStatusCode("200");
						response.setMessage("Successful!");
					} else {
						response.setStatusCode("500");
						response.setMessage("Internal Server Errror!");
					}
					bufferedOutputWriter.write(gson.toJson(response));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else {
					SimpleResponse response = new SimpleResponse();
					response.setStatusCode("404");
					response.setMessage("Command not Found");
					bufferedOutputWriter.write(gson.toJson(response));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				}

			} catch (SocketTimeoutException ste) {
				// timeout every .25 seconds to see if interrupted
			}
		}

	}

	@Override
	public void run() {
		try {
			accept();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
