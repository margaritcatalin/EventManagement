package com.unitbv.events.response;

import java.util.List;

import com.unitbv.events.data.EventData;

public class EventDataResponse {
	String statusCode;
	List<EventData> events;

	public List<EventData> getEvents() {
		return events;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setEvents(List<EventData> events) {
		this.events = events;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
