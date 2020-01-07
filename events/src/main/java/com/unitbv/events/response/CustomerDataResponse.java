package com.unitbv.events.response;

import java.util.List;

import com.unitbv.events.data.CustomerData;

public class CustomerDataResponse {
	String statusCode;
	List<CustomerData> customers;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public List<CustomerData> getCustomers() {
		return customers;
	}

	public void setCustomers(List<CustomerData> customers) {
		this.customers = customers;
	}
}
