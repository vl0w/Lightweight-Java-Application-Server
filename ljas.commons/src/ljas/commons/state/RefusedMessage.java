package ljas.commons.state;

import java.io.Serializable;

public class RefusedMessage implements Serializable {
	private static final long serialVersionUID = 6599405984948483674L;

	public static RefusedMessage SERVER_FULL = new RefusedMessage(
			"Server is full");
	public static RefusedMessage ILLEGAL_STATE = new RefusedMessage(
			"Server has illegal state");
	public static RefusedMessage WRONG_APPLICATION_VERSION = new RefusedMessage(
			"Client has not same application version as server");
	public static RefusedMessage WRONG_APPLICATION_ID = new RefusedMessage(
			"Client has not same application as server");
	public static RefusedMessage UNKNOWN_EXCEPTION_OCCURED = new RefusedMessage(
			"Unknown server exception occured");

	private final String _reason;

	public String getReason() {
		return _reason;
	}

	public RefusedMessage(String reason) {
		_reason = reason;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RefusedMessage) {
			RefusedMessage rmObj = (RefusedMessage) obj;
			if (rmObj.getReason().equals(getReason())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getReason().hashCode();
	}
}
