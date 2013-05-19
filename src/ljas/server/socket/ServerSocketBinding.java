package ljas.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

public class ServerSocketBinding {

	private ServerSocket serverSocket;
	private ExecutorService acceptorService;

	public ServerSocketBinding(ServerSocket serverSocket,
			ExecutorService acceptorService) {
		this.serverSocket = serverSocket;
		this.acceptorService = acceptorService;
	}

	public void unbind() throws IOException {
		acceptorService.shutdownNow();
		serverSocket.close();
	}
}
