package ljas.exception;

public class SessionException extends Exception {
	private static final long serialVersionUID = 2680240287089474322L;

	public SessionException() {
	}

	public SessionException(String message) {
		super(message);
	}

	public SessionException(Throwable cause) {
		super(cause);
	}

	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}
}
