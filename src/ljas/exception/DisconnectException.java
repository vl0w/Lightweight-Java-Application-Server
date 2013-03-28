package ljas.exception;

public class DisconnectException extends Exception {

	private static final long serialVersionUID = 4731954858525396395L;

	public DisconnectException() {
	}

	public DisconnectException(String message) {
		super(message);
	}

	public DisconnectException(Throwable cause) {
		super(cause);
	}

	public DisconnectException(String message, Throwable cause) {
		super(message, cause);
	}

	public DisconnectException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
