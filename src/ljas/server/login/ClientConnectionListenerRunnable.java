package ljas.server.login;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import ljas.commons.session.Session;
import ljas.commons.session.SessionFactory;
import ljas.commons.threading.RepeatingRunnable;
import ljas.server.Server;

public class ClientConnectionListenerRunnable extends RepeatingRunnable {

	private Server server;

	public ClientConnectionListenerRunnable(Server server) {
		this.server = server;
	}

	@Override
	protected void runCycle() {
		try {
			Socket clientSocket = server.getServerSocket().accept();

			Session clientSession = SessionFactory
					.createSocketSession(clientSocket);

			ServerLoginSessionObserver serverLoginSessionObserver = new ServerLoginSessionObserver(
					server);
			clientSession.setObserver(serverLoginSessionObserver);

		} catch (SocketTimeoutException e) {
			// Log nothing, let it be
		} catch (IOException e) {
			// Log nothing, let it be
		} catch (Exception e) {
			server.getLogger().error(e);
		}
	}
}
