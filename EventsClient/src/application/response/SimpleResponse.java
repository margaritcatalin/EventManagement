package application.response;

public class SimpleResponse {

	String statusCode;
	String message;

	public String getMessage() {
		return message;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
