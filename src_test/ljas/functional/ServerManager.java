package ljas.functional;

import java.io.IOException;

import ljas.commons.application.Application;
import ljas.functional.application.TestApplicationImpl;
import ljas.server.Server;

public abstract class ServerManager {
	private static Server server = null;

	public static Server getServer() throws IOException {
		if (server == null) {
			server = createServer();
		}
		return server;
	}

	public static Server createServer() throws IOException {
		Application serverApplication = new TestApplicationImpl();
		return new Server(serverApplication, new TestingServerConfiguration());
	}

	// TODO exception handling
	public static void startupServer() throws Exception {
		Server server = getServer();
		if (server.isOnline()) {
			server.shutdown();
		}
		server.startup();
	}

	// TODO exception handling
	public static void shutdownServer() throws IOException {
		Server server = getServer();
		if (server.isOnline()) {
			server.shutdown();
		}
	}
}
