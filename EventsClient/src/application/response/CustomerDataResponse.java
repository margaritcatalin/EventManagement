package application.response;

import application.data.CustomerData;
import application.data.InvitationData;

import java.util.List;

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
