package ljas.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

public class ServerSocketBinding implements AutoCloseable {

	private ServerSocket serverSocket;
	private ExecutorService service;

	public ServerSocketBinding(ServerSocket serverSocket, ExecutorService service) {
		this.serverSocket = serverSocket;
		this.service = service;
	}

	@Override
	public void close() throws IOException {
		serverSocket.close();
		service.shutdownNow();
	}
}
