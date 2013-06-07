package ljas.server.state;

import java.util.ArrayList;
import java.util.List;

import ljas.application.ApplicationAnalyzer;
import ljas.application.LoginParameters;
import ljas.exception.ConnectionRefusedException;
import ljas.server.Server;
import ljas.server.configuration.Property;
import ljas.session.Session;
import ljas.state.login.LoginRefusedMessage;

import org.apache.log4j.Logger;

public class OnlineState implements ServerState {

	private Server server;

	public OnlineState(Server server) {
		this.server = server;
	}

	@Override
	public void checkClient(LoginParameters parameters)
			throws ConnectionRefusedException {
		// Is the server full?
		int maximumClients = Integer.valueOf(server.getProperties()
				.get(Property.MAXIMUM_CLIENTS).toString());
		if (server.getSessions().size() >= maximumClients) {
			throw new ConnectionRefusedException(
					LoginRefusedMessage.SERVER_FULL);
		}

		// Check application
		if (!ApplicationAnalyzer.areApplicationsEqual(parameters
				.getClientApplicationClass(), server.getApplication()
				.getClass())) {
			throw new ConnectionRefusedException(
					LoginRefusedMessage.INVALID_APPLICATION);
		}

	}

	@Override
	public void startup() {
		throw new IllegalStateException();
	}

	@Override
	public void shutdown() {
		try {
			getLogger().debug("Unbinding server sockets");
			server.getServerSocketBinder().close();

			getLogger().info("Closing client sessions");
			List<Session> sessionsToClose = new ArrayList<>(
					server.getSessions());
			for (Session session : sessionsToClose) {
				session.disconnect();
			}

			getLogger().debug("Shutdown executors");
			server.getTaskSystem().shutdown();

			getLogger().info(server + " is offline");
		} catch (Exception e) {
			getLogger().error(e);
		}
	}

	private Logger getLogger() {
		return server.getLogger();
	}

}
