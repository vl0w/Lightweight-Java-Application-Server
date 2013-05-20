package ljas.server;

import ljas.exception.ApplicationException;
import ljas.session.Session;
import ljas.tasking.environment.TaskSystemSessionObserver;

public class ServerTasksystemSessionObserver extends TaskSystemSessionObserver {

	private Server server;

	public ServerTasksystemSessionObserver(Server server) {
		super(server.getTaskSystem());
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
