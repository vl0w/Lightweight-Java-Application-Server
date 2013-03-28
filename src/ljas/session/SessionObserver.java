package ljas.session;

public interface SessionObserver {
	public void notiyObjectReceived(Session session, Object obj);

	public void notifySessionDisconnected(Session session);
}
