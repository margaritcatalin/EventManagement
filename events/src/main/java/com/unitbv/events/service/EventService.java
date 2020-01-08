package com.unitbv.events.service;

import com.unitbv.events.request.CreateEventRequest;
import com.unitbv.events.request.EditEventRequest;
import com.unitbv.events.request.SendInvitationRequest;
import com.unitbv.events.response.EventDataResponse;
import com.unitbv.events.response.SimpleResponse;

public interface EventService {

	EventDataResponse getAllEvents(String currentUserEmail);

	EventDataResponse getNextEventsForCurrentUser(String currentUserEmail);

	EventDataResponse getCompletedEventsForCurrentUser(String currentUserEmail);

	boolean createEvent(CreateEventRequest request);

	boolean editEvent(EditEventRequest request);

	SimpleResponse deleteEvent(String eventCode);
	
	EventDataResponse getEventById(String eventId);
	boolean sendInvitation(SendInvitationRequest sendInvitationRequest);
}
