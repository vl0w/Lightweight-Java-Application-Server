package ljas.session.observer;

import ljas.session.Session;

public interface SessionDataObserver {
	void onObjectReceived(Session session, Object obj);
}
