package ljas.functional;

import java.io.IOException;

import ljas.commons.application.server.ServerApplicationAdapter;
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
		return new Server(new ServerApplicationAdapter("ljas.testing", "1.0"),
				new TestingServerConfiguration());
	}

	// TODO exception handling
	public static void startupServer() throws Exception {
		Server server = getServer();
		if (!server.isOnline()) {
			server.startup();
		}
	}

	// TODO exception handling
	public static void shutdownServer() throws IOException {
		Server server = getServer();
		if (server.isOnline()) {
			server.shutdown();
		}
	}
}
