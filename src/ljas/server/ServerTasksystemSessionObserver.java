package ljas.server;

import ljas.commons.application.server.ServerApplicationException;
import ljas.commons.session.Session;
import ljas.commons.tasking.environment.TaskSystemSessionObserver;

public class ServerTasksystemSessionObserver extends TaskSystemSessionObserver {

	private Server server;

	public ServerTasksystemSessionObserver(Server server) {
		super(server.getTaskSystem());
		this.server = server;
	}

	@Override
	public void notifySessionDisconnected(Session session) {
		// Remove from Application
		try {
			server.getApplication().removeUser(session);
		} catch (ServerApplicationException e) {
			server.getLogger().error(
					"Error while deleting user from application", e);
		}

		// Remove from client list
		if (!server.getSessions().remove(session)) {
			server.getLogger().error("Unable to delete session from server");
		}

		server.getLogger().info(
				"Client disconnected (" + session + "), "
						+ (server.getSessions().size())
						+ " connection(s) overall");
	}
}
