package ljas.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import ljas.threading.RepeatingRunnable;

import org.apache.log4j.Logger;

public class ServerSocketAcceptor extends RepeatingRunnable {

	private ServerSocket serverSocket;
	private LoginObserver observer;

	public ServerSocketAcceptor(ServerSocket serverSocket,
			LoginObserver observer) {
		this.serverSocket = serverSocket;
		this.observer = observer;
	}

	@Override
	protected void runCycle() {
		try {
			Socket clientSocket = serverSocket.accept();
			observer.onSocketConnect(clientSocket);

		} catch (SocketTimeoutException e) {
			// Log nothing, let it be
		} catch (IOException e) {
			// Log nothing, let it be
		} catch (Exception e) {
			Logger.getLogger(getClass()).error(e);
		}
	}
}
