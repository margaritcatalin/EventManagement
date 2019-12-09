package com.unitbv.events.service;

import com.unitbv.events.request.CreateEventRequest;
import com.unitbv.events.response.EventDataResponse;

public interface EventService {

	EventDataResponse getAllEvents();
	
	boolean createEvent(CreateEventRequest request);


}
