package ljas.session;

import ljas.session.observer.NullDataObserver;
import ljas.session.observer.NullDisconnectObserver;
import ljas.session.observer.SessionDataObserver;
import ljas.session.observer.SessionDisconnectObserver;

public abstract class AbstractSession implements Session {

	private SessionDataObserver dataObserver;
	private SessionDisconnectObserver disconnectObserver;

	public AbstractSession() {
		dataObserver = new NullDataObserver();
		disconnectObserver = new NullDisconnectObserver();
	}

	@Override
	public void setDataObserver(SessionDataObserver observer) {
		dataObserver = observer;
	}

	public SessionDataObserver getDataObserver() {
		return dataObserver;
	}

	@Override
	public void setDisconnectObserver(SessionDisconnectObserver observer) {
		disconnectObserver = observer;
	}

	public SessionDisconnectObserver getDisconnectObserver() {
		return disconnectObserver;
	}
}
