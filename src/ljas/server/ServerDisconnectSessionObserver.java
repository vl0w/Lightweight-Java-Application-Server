package ljas.server;

import ljas.exception.ApplicationException;
import ljas.session.Session;
import ljas.session.observer.SessionDisconnectObserver;

public class ServerDisconnectSessionObserver implements
		SessionDisconnectObserver {

	private Server server;

	public ServerDisconnectSessionObserver(Server server) {
		this.server = server;
	}

	@Override
	public void onSessionDisconnected(Session session) {
		server.getSessions().remove(session);

		// Remove from Application
		try {
			server.getApplication().onSessionDisconnect(session);
		} catch (ApplicationException e) {
			server.getLogger().error(
					"Error while deleting user from application", e);
		}

		server.getLogger().info(
				"Client disconnected (" + session + "), "
						+ (server.getSessions().size())
						+ " connection(s) overall");
	}
}
