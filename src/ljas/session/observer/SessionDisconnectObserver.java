package ljas.session.observer;

import ljas.session.Session;

public interface SessionDisconnectObserver {
	void onSessionDisconnected(Session session);
}
