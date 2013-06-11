package ljas.session;

import ljas.exception.SessionException;
import ljas.session.observer.SessionDataObserver;
import ljas.session.observer.SessionDisconnectObserver;

public interface Session extends Disconnectable {
	void connect(Address address) throws SessionException;

	void sendObject(Object obj) throws SessionException;

	void setDataObserver(SessionDataObserver observer);

	void setDisconnectObserver(SessionDisconnectObserver observer);

	boolean isConnected();
}
