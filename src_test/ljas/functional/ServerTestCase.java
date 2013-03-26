package ljas.functional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ljas.client.Client;
import ljas.client.ClientImpl;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.exceptions.SessionException;
import ljas.functional.application.TestApplication;
import ljas.server.Server;

import org.junit.After;
import org.junit.Before;

public class ServerTestCase {
	public static final int TEST_TIMEOUT = 10000;
	public static final String APPLICATION_IDENTIFIER = "ljas.testing";
	public static final String APPLICATION_VERSION = "1.0";

	@Before
	public void setUp() throws Exception {
		ServerManager.startupServer();
		assertTrue("Server is not online!", ServerManager.getServer()
				.isOnline());
	}

	@After
	public void tearDown() throws Exception {
		ServerManager.shutdownServer();
		Server server = ServerManager.getServer();
		assertFalse("Server is still online!", server.isOnline());
	}

	public static Client createAndConnectClient()
			throws ConnectionRefusedException, SessionException {
		return createAndConnectClient(APPLICATION_IDENTIFIER,
				APPLICATION_VERSION);
	}

	public static void connectClient(Client client)
			throws ConnectionRefusedException, SessionException {
		client.connect("localhost", 1666);
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
		int maximumClients = ServerManager.getServer().getConfiguration()
				.getMaximumClients();
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

}
