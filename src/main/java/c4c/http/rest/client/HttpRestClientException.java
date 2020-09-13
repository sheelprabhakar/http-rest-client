package c4c.http.rest.client;

public class HttpRestClientException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int responseStatus;

	public HttpRestClientException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public HttpRestClientException(final String message, final int status) {
		super(message);
		responseStatus = status;
	}

	public int getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(final int responseStatus) {
		this.responseStatus = responseStatus;
	}

}
