package ljas.commons.state;

import java.io.Serializable;

import ljas.commons.network.ConnectionInfo;

public class WelcomeMessage implements Serializable {
	private static final long serialVersionUID = 5359705297664395635L;

	private String _hostName;
	private String _messageOfTheDay;
	private ConnectionInfo _connectionInfo;

	public String getHostName() {
		return _hostName;
	}

	public void setHostName(String value) {
		_hostName = value;
	}

	public String getMessageOfTheDay() {
		return _messageOfTheDay;
	}

	public void setMessageOfTheDay(String value) {
		_messageOfTheDay = value;
	}

	public ConnectionInfo getConnectionInfo() {
		return _connectionInfo;
	}

	public void setConnectionInfo(ConnectionInfo info) {
		_connectionInfo = info;
	}

	public WelcomeMessage(String serverName, String messageOfTheDay) {
		setHostName(serverName);
		setMessageOfTheDay(messageOfTheDay);
		setConnectionInfo(null);
	}
}
