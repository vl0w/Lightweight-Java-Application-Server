package ljas.commons.state.login;

public class LoginAcceptedMessage implements LoginMessage {
	private static final long serialVersionUID = 5359705297664395635L;

	private String serverName;

	public String getHostName() {
		return serverName;
	}

	public void setHostName(String value) {
		serverName = value;
	}

	public LoginAcceptedMessage(String serverName) {
		this.serverName = serverName;
	}
}
