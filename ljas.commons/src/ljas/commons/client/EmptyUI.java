package ljas.commons.client;

import ljas.commons.state.RefusedMessage;
import ljas.commons.state.WelcomeMessage;

public class EmptyUI implements ClientUI {
	private boolean _printStatus;
	private static final String PREFIX = ">>>";

	public EmptyUI(boolean printStatus) {
		_printStatus = printStatus;
	}

	public EmptyUI() {
		this(false);
	}

	@Override
	public void handleStart() {
		// nothing
	}

	@Override
	public Client getClient() {
		// nothing
		return null;
	}

	@Override
	public void setClient(Client client) {
		// nothing
	}

	@Override
	public void handleConnected(WelcomeMessage welcomeMessage) {
		if (_printStatus) {
			System.out.println(PREFIX + "You are now connected");
			System.out.println(PREFIX + "Server: "
					+ welcomeMessage.getHostName());
			System.out.println(PREFIX + "MOTD: "
					+ welcomeMessage.getMessageOfTheDay());
		}
	}

	@Override
	public void handleConnectionFailed(RefusedMessage refusedMessage) {
		if (_printStatus) {
			System.out.println(PREFIX + "Connection refused");
			System.out.println(PREFIX + refusedMessage.getReason());
		}
	}

	@Override
	public void handleDisconnected() {
		if (_printStatus) {
			System.out.println(PREFIX + "You have been disconnected");
		}
	}
}
