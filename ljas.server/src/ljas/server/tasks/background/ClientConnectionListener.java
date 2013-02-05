package ljas.server.tasks.background;

import java.io.IOException;
import java.net.SocketTimeoutException;
import ljas.commons.application.LoginParameters;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.network.ConnectionInfo;
import ljas.commons.network.SocketConnection;
import ljas.commons.network.TaskSender;
import ljas.commons.state.RefusedMessage;
import ljas.commons.state.WelcomeMessage;
import ljas.commons.tasking.Task;
import ljas.server.Server;

public class ClientConnectionListener extends Task {
	private static final long serialVersionUID = -3839564995881410292L;

	public ClientConnectionListener(TaskSender local) {
		setLocal(local);
	}

	@Override
	public void performTask() {
		try {
			// Get the clients socket
			SocketConnection clientConnection = new SocketConnection(
					((Server) getLocal()).getServerSocket().accept(),
					getLocal());

			getLocal().getLogger().debug(
					"New connection incoming (" + clientConnection + ")");

			LoginParameters parameters = (LoginParameters) clientConnection
					.readObject();

			try {
				((Server) getLocal()).checkClient(parameters);
				((Server) getLocal()).registerClient(clientConnection,
						parameters);

				// Ok
				getLocal().getLogger().info(
						"New connection accepted ("
								+ clientConnection
								+ "), "
								+ ((Server) getLocal()).getConnectedClients()
										.size() + " connection(s) overall");

				// Create welcome-message
				WelcomeMessage welcome = new WelcomeMessage(
						((Server) getLocal()).getConfiguration().getHostName(),
						((Server) getLocal()).getConfiguration()
								.getMessasgeOfTheDay());
				String remoteIp = clientConnection.getSocket().getInetAddress()
						.toString();
				int remotePort = clientConnection.getSocket().getPort();
				welcome.setConnectionInfo(new ConnectionInfo(remoteIp,
						remotePort));

				// Send answer
				clientConnection.writeObject(welcome);
				clientConnection.getSocket().setKeepAlive(true);

			} catch (ConnectionRefusedException cre) {
				getLocal().getLogger().info(
						"New connection refused (" + clientConnection + "), "
								+ cre.getRefusedMessage().getReason());
				clientConnection.writeObject(cre.getRefusedMessage());
			} catch (Exception e) {
				getLocal().getLogger().info(
						"New connection refused (" + clientConnection + ")");
				clientConnection
						.writeObject(RefusedMessage.UNKNOWN_EXCEPTION_OCCURED);
			}

		} catch (SocketTimeoutException e) {
			// Log nothing, let it be
		} catch (IOException e) {
			// Log nothing, let it be
		} catch (Exception e) {
			getLocal().getLogger().error(e);
		}
	}
}
