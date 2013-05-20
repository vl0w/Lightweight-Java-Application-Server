package ljas.session;

public interface SessionObserver {
	public void onObjectReceived(Session session, Object obj);

	public void onSessionDisconnected(Session session);
}
