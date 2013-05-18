package ljas.session;

import ljas.exception.SessionException;

public interface Session extends Disconnectable {
	void connect(Address address) throws SessionException;

	void sendObject(Object obj) throws SessionException;

	void setObserver(SessionObserver observer);

	boolean isConnected();
}
