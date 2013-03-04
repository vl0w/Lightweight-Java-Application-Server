package ljas.server.login;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import ljas.commons.session.SessionFactory;
import ljas.server.Server;

public class ClientConnectionListener implements Runnable {

	private Server server;

	public ClientConnectionListener(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		try {
			Socket clientSocket = server.getServerSocket().accept();

			ServerLoginSessionObserver serverLoginSessionObserver = new ServerLoginSessionObserver(
					server);
			SessionFactory.createSocketSession(server.getThreadSystem(),
					clientSocket, serverLoginSessionObserver);
			// new Thread(new OnConnect(clientSocket)).start();
		} catch (SocketTimeoutException e) {
			// Log nothing, let it be
		} catch (IOException e) {
			// Log nothing, let it be
		} catch (Exception e) {
			server.getLogger().error(e);
		}
	}
}
