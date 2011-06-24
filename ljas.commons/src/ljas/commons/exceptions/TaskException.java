package ljas.commons.exceptions;

public class TaskException extends Exception {
	private static final long serialVersionUID = -4273081067655037203L;

	public TaskException() {
		super();
	}

	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskException(String message) {
		super(message);
	}

	public TaskException(Throwable cause) {
		super(cause);

	}
}
