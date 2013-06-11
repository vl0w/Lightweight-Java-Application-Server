package ljas.server;

import ljas.application.LoginParameters;
import ljas.exception.ApplicationException;
import ljas.exception.ConnectionRefusedException;
import ljas.exception.SessionException;
import ljas.session.Session;
import ljas.session.observer.SessionDataObserver;
import ljas.state.login.LoginAcceptedMessage;
import ljas.state.login.LoginRefusedMessage;
import ljas.tasking.environment.TaskSystemSessionDataObserver;

public class ServerLoginSessionDataObserver implements SessionDataObserver {
	private Server server;

	public ServerLoginSessionDataObserver(Server server) {
		this.server = server;
	}

	@Override
	public void onObjectReceived(Session session, Object obj) {
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

			// Handling the disconnection of the session
			ServerDisconnectSessionObserver observer = new ServerDisconnectSessionObserver(
					server);
			session.setDisconnectObserver(observer);

			// Handling new incoming data
			session.setDataObserver(new TaskSystemSessionDataObserver(server
					.getTaskSystem()));

			// Create welcome-message
			LoginAcceptedMessage welcome = new LoginAcceptedMessage();

			// Send answer
			session.sendObject(welcome);
		} catch (ConnectionRefusedException cre) {
			server.getLogger().info(
					"New connection refused (" + session + "), "
							+ cre.getRefusedMessage().getReason());
			sendErrorAnswer(session, cre.getRefusedMessage());
		} catch (ApplicationException e) {
			server.getLogger().error(
					"New connection refused (" + session
							+ ") due to error in application");
			server.getLogger().error(e);
			sendErrorAnswer(session,
					LoginRefusedMessage.UNKNOWN_EXCEPTION_OCCURED);
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
}
