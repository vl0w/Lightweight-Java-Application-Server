package ljas.commons.session;

import ljas.commons.exceptions.SessionException;

public interface Session extends Disconnectable {
	void connect(String ip, int port) throws SessionException;

	void sendObject(Object obj) throws SessionException;

	void setObserver(SessionObserver observer);

	boolean isConnected();
}
