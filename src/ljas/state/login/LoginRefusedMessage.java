package ljas.state.login;

public class LoginRefusedMessage implements LoginMessage {
	private static final long serialVersionUID = 6599405984948483674L;

	public static final LoginRefusedMessage SERVER_FULL = new LoginRefusedMessage(
			"Server is full");
	public static final LoginRefusedMessage ILLEGAL_STATE = new LoginRefusedMessage(
			"Server has illegal state");
	public static final LoginRefusedMessage INVALID_APPLICATION = new LoginRefusedMessage(
			"Client does not use the same application as the server");
	public static final LoginRefusedMessage UNKNOWN_EXCEPTION_OCCURED = new LoginRefusedMessage(
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
