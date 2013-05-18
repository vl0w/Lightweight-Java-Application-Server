package ljas.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ljas.client.Client;
import ljas.client.ClientImpl;
import ljas.exception.ConnectionRefusedException;
import ljas.exception.SessionException;
import ljas.integration.application.TestApplication;
import ljas.integration.application.TestApplicationImpl;
import ljas.server.Server;
import ljas.server.configuration.Property;

import org.junit.After;
import org.junit.Before;

public class ServerTestCase {
	public static final int TEST_TIMEOUT = 10000;
	public static final String APPLICATION_IDENTIFIER = "ljas.testing";
	public static final String APPLICATION_VERSION = "1.0";
	private Server server;

	@Before
	public void startServer() throws Exception {
		server = new Server(new TestApplicationImpl());

		// Set properties
		server.getProperties().set(Property.MAXIMUM_CLIENTS, 5);

		server.startup();

		assertTrue("Server is not online!", server.isOnline());
	}

	@After
	public void stopServer() throws Exception {
		if (server.isOnline()) {
			server.shutdown();
			server.getProperties().reset();
		}

		assertFalse("Server is still online!", server.isOnline());
	}

	public static Client createAndConnectClient()
			throws ConnectionRefusedException, SessionException {
		return createAndConnectClient(APPLICATION_IDENTIFIER,
				APPLICATION_VERSION);
	}

	public static void connectClient(Client client)
			throws ConnectionRefusedException, SessionException {
		client.connect("localhost", 7755);
	}

	public static Client createClient() throws SessionException,
			ConnectionRefusedException {
		return new ClientImpl(TestApplication.class);
	}

	protected static Client createAndConnectClient(
			String applicationIdentifier, String applicationVersion)
			throws ConnectionRefusedException, SessionException {
		Client client = createClient();
		connectClient(client);
		return client;
	}

	protected List<Client> createClients() throws ConnectionRefusedException,
			SessionException, IOException {
		int maximumClients = server.getProperties().get(
				Property.MAXIMUM_CLIENTS);
		return createClients(maximumClients);
	}

	protected List<Client> createClients(int amount)
			throws ConnectionRefusedException, SessionException {
		List<Client> clients = new ArrayList<>();

		// Connect maximum amount of clients
		for (int i = 0; i < amount; i++) {
			clients.add(createAndConnectClient());
		}

		return clients;
	}

	protected Server getServer() {
		return server;
	}
}
