package ljas.server.login;

import ljas.commons.application.LoginParameters;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.exceptions.SessionException;
import ljas.commons.session.Session;
import ljas.commons.session.SessionObserver;
import ljas.commons.state.login.LoginAcceptedMessage;
import ljas.commons.state.login.LoginRefusedMessage;
import ljas.server.Server;
import ljas.server.ServerTasksystemSessionObserver;

public class ServerLoginSessionObserver implements SessionObserver {
	private Server server;

	public ServerLoginSessionObserver(Server server) {
		this.server = server;
	}

	@Override
	public void notiyObjectReceived(Session session, Object obj) {

		try {
			LoginParameters parameters = (LoginParameters) obj;

			server.checkClient(parameters);
			server.addSession(session);
			server.getApplication().onSessionConnect(session, parameters);

			// Ok
			server.getLogger().info(
					"New connection accepted (" + session + "), "
							+ server.getSessions().size()
							+ " connection(s) overall");

			// Create welcome-message
			LoginAcceptedMessage welcome = new LoginAcceptedMessage(server
					.getConfiguration().getHostName());

			// Send answer
			session.sendObject(welcome);

			// Create new Observer
			ServerTasksystemSessionObserver observer = new ServerTasksystemSessionObserver(
					server);
			session.setObserver(observer);
		} catch (ConnectionRefusedException cre) {
			server.getLogger().info(
					"New connection refused (" + session + "), "
							+ cre.getRefusedMessage().getReason());
			sendErrorAnswer(session, cre.getRefusedMessage());
		} catch (Exception e) {
			server.getLogger().info("New connection refused (" + session + ")");
			sendErrorAnswer(session,
					LoginRefusedMessage.UNKNOWN_EXCEPTION_OCCURED);
		}
	}

	private void sendErrorAnswer(Session session,
			LoginRefusedMessage refusedMessage) {
		try {
			session.sendObject(refusedMessage);
		} catch (SessionException e) {
			server.getLogger().error(
					"Error while refusing client login request", e);
		}
	}

	@Override
	public void notifySessionDisconnected(Session session) {
		// TODO Auto-generated method stub

	}

}
