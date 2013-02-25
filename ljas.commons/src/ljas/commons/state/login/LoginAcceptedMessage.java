package ljas.commons.state.login;


public class LoginAcceptedMessage implements LoginMessage {
	private static final long serialVersionUID = 5359705297664395635L;

	private String serverName;
	private String messageOfTheDay;

	public String getHostName() {
		return serverName;
	}

	public void setHostName(String value) {
		serverName = value;
	}

	public String getMessageOfTheDay() {
		return messageOfTheDay;
	}

	public void setMessageOfTheDay(String value) {
		messageOfTheDay = value;
	}

	public LoginAcceptedMessage(String serverName, String messageOfTheDay) {
		this.serverName = serverName;
		this.messageOfTheDay = messageOfTheDay;
	}
}
