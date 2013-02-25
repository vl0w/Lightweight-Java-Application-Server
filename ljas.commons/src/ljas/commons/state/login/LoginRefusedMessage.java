package ljas.commons.state.login;

public class LoginRefusedMessage implements LoginMessage {
	private static final long serialVersionUID = 6599405984948483674L;

	public static LoginRefusedMessage SERVER_FULL = new LoginRefusedMessage(
			"Server is full");
	public static LoginRefusedMessage ILLEGAL_STATE = new LoginRefusedMessage(
			"Server has illegal state");
	public static LoginRefusedMessage WRONG_APPLICATION_VERSION = new LoginRefusedMessage(
			"Client has not same application version as server");
	public static LoginRefusedMessage WRONG_APPLICATION_ID = new LoginRefusedMessage(
			"Client has not same application as server");
	public static LoginRefusedMessage UNKNOWN_EXCEPTION_OCCURED = new LoginRefusedMessage(
			"Unknown server exception occured");

	private final String reason;

	public String getReason() {
		return reason;
	}

	public LoginRefusedMessage(String reason) {
		this.reason = reason;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LoginRefusedMessage) {
			LoginRefusedMessage rmObj = (LoginRefusedMessage) obj;
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
