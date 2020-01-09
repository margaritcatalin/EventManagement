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
import com.unitbv.events.request.AcceptInvitationRequest;
import com.unitbv.events.request.AvailabilityRequest;
import com.unitbv.events.request.CreateEventRequest;
import com.unitbv.events.request.EditEventRequest;
import com.unitbv.events.request.EditProfileRequest;
import com.unitbv.events.request.GetUserRequest;
import com.unitbv.events.request.LoginRequst;
import com.unitbv.events.request.RegisterRequest;
import com.unitbv.events.request.SendInvitationRequest;
import com.unitbv.events.response.SimpleResponse;
import com.unitbv.events.service.EventService;
import com.unitbv.events.service.InvitationService;
import com.unitbv.events.service.NotificationService;
import com.unitbv.events.service.UserService;
import com.unitbv.events.service.impl.DefaultEventService;
import com.unitbv.events.service.impl.DefaultInvitationService;
import com.unitbv.events.service.impl.DefaultNotificationService;
import com.unitbv.events.service.impl.DefaultUserService;

public class ServerUtilities implements Runnable {
	private ServerSocket serverSocket;
	private Gson gson;
	private UserService userService;
	private EventService eventService;
	private InvitationService invitationService;
	private NotificationService notificationService;

	public ServerUtilities(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		gson = new Gson();
		userService = new DefaultUserService();
		eventService = new DefaultEventService();
		invitationService = new DefaultInvitationService();
		notificationService = new DefaultNotificationService();
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
					} else if (!userService.checkUserStatus(request.getEmail())) {
						response.setStatusCode("500");
						response.setMessage("This user is deactivated!");
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
				} else if ("acceptInvitation".equalsIgnoreCase(inputCommand)) {
					AcceptInvitationRequest acceptInvitationRequest = gson.fromJson(receivedData,
							AcceptInvitationRequest.class);
					bufferedOutputWriter.write(gson.toJson(eventService.acceptInvitation(acceptInvitationRequest)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getAllEvents".equalsIgnoreCase(inputCommand)) {
					String currentUserEmail = receivedData;
					bufferedOutputWriter.write(gson.toJson(eventService.getAllEvents(currentUserEmail)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getCompletedEventsForCurrentUser".equalsIgnoreCase(inputCommand)) {
					String currentUserEmail = receivedData;
					bufferedOutputWriter
							.write(gson.toJson(eventService.getCompletedEventsForCurrentUser(currentUserEmail)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getNextEventsForCurrentUser".equalsIgnoreCase(inputCommand)) {
					String currentUserEmail = receivedData;
					bufferedOutputWriter.write(gson.toJson(eventService.getNextEventsForCurrentUser(currentUserEmail)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getNotificationForCurrentUser".equalsIgnoreCase(inputCommand)) {
					String currentUserEmail = receivedData;
					bufferedOutputWriter.write(gson.toJson(userService.getAllNotificationForUser(currentUserEmail)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getInvitationsForCurrentUser".equalsIgnoreCase(inputCommand)) {
					String currentUserEmail = receivedData;
					bufferedOutputWriter.write(gson.toJson(userService.getAllInvitationForUser(currentUserEmail)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getInvitationByCode".equalsIgnoreCase(inputCommand)) {
					String invitationCode = receivedData;
					bufferedOutputWriter.write(gson.toJson(invitationService.getInvitationByCode(invitationCode)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getAllUsers".equalsIgnoreCase(inputCommand)) {
					bufferedOutputWriter.write(gson.toJson(userService.getAllUsers()));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getAllCustomers".equalsIgnoreCase(inputCommand)) {
					bufferedOutputWriter.write(gson.toJson(userService.getAllCustomers()));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getAllOrganizers".equalsIgnoreCase(inputCommand)) {
					bufferedOutputWriter.write(gson.toJson(userService.getAllOrganizers()));
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
				} else if ("deleteEvent".equalsIgnoreCase(inputCommand)) {
					String eventCode = receivedData;
					bufferedOutputWriter.write(gson.toJson(eventService.deleteEvent(eventCode)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("deleteAllNotification".equalsIgnoreCase(inputCommand)) {
					String userCode = receivedData;
					bufferedOutputWriter.write(gson.toJson(notificationService.deleteAllNotification(userCode)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("deleteNotification".equalsIgnoreCase(inputCommand)) {
					String notificationCode = receivedData;
					bufferedOutputWriter.write(gson.toJson(notificationService.deleteNotification(notificationCode)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("editEvent".equalsIgnoreCase(inputCommand)) {
					SimpleResponse response = new SimpleResponse();
					EditEventRequest editEventRequest = gson.fromJson(receivedData, EditEventRequest.class);
					if (eventService.editEvent(editEventRequest)) {
						response.setStatusCode("200");
						response.setMessage("Successful!");
					} else {
						response.setStatusCode("500");
						response.setMessage("Internal Server Errror!");
					}
					bufferedOutputWriter.write(gson.toJson(response));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getEventById".equalsIgnoreCase(inputCommand)) {
					String eventId = receivedData;
					bufferedOutputWriter.write(gson.toJson(eventService.getEventById(eventId)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("getEventByInvitationId".equalsIgnoreCase(inputCommand)) {
					bufferedOutputWriter.write(gson.toJson(eventService.getEventByInvitationId(receivedData)));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();
				} else if ("sendInvitationToUser".equalsIgnoreCase(inputCommand)) {
					SimpleResponse response = new SimpleResponse();
					SendInvitationRequest sendInvitationRequest = gson.fromJson(receivedData,
							SendInvitationRequest.class);
					if (eventService.sendInvitation(sendInvitationRequest)) {
						response.setStatusCode("200");
						response.setMessage("Successful!");
					} else {
						response.setStatusCode("500");
						response.setMessage("Internal Server Errror!");
					}
					bufferedOutputWriter.write(gson.toJson(response));
					bufferedOutputWriter.newLine();
					bufferedOutputWriter.flush();

				} else if ("deactivateUser".equalsIgnoreCase(inputCommand)) {
					SimpleResponse response = new SimpleResponse();
					if (userService.checkIfAccountExist(receivedData) && userService.deactivateUser(receivedData)) {
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
