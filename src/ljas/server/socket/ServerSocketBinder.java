package ljas.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketBinder implements AutoCloseable {

	private static final int ACCEPTOR_THREADS_PER_BINDING = 5;
	private Map<Integer, ServerSocketBinding> bindings;

	public ServerSocketBinder() {
		bindings = new HashMap<>();
	}

	public void bind(int port, LoginObserver observer) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		ExecutorService service = createAcceptorService(observer, serverSocket);

		ServerSocketBinding binding = new ServerSocketBinding(serverSocket,
				service);
		bindings.put(port, binding);
	}

	@Override
	public void close() throws IOException {
		for (ServerSocketBinding binding : bindings.values()) {
			binding.close();
		}
		bindings = new HashMap<>();
	}

	private ExecutorService createAcceptorService(LoginObserver observer,
			ServerSocket serverSocket) {
		ExecutorService service = Executors
				.newFixedThreadPool(ACCEPTOR_THREADS_PER_BINDING);

		for (int i = 0; i < ACCEPTOR_THREADS_PER_BINDING; i++) {
			ServerSocketAcceptor socketAcceptor = new ServerSocketAcceptor(
					serverSocket, observer);
			service.submit(socketAcceptor);
		}
		return service;
	}

}
