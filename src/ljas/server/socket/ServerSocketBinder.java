package ljas.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketBinder {

	private Map<Integer, ServerSocketBinding> bindings;

	public ServerSocketBinder() {
		bindings = new HashMap<>();
	}

	public void bind(int port, LoginObserver observer) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		ExecutorService acceptorService = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 5; i++) {
			ServerSocketAcceptor socketAcceptor = new ServerSocketAcceptor(
					serverSocket, observer);
			acceptorService.submit(socketAcceptor);
		}

		ServerSocketBinding binding = new ServerSocketBinding(serverSocket,
				acceptorService);
		bindings.put(port, binding);
	}

	public void unbindAll() throws IOException {
		for (ServerSocketBinding binding : bindings.values()) {
			binding.unbind();
		}
		bindings = new HashMap<>();
	}
}
