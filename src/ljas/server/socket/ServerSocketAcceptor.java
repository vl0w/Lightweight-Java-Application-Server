package ljas.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ljas.threading.RepeatingRunnable;

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
		} catch (IOException e) {
			// Swallow exceptions
		}
	}
}
