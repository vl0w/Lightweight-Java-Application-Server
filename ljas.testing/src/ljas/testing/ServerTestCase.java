package ljas.testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ljas.commons.application.LoginParameters;
import ljas.commons.application.LoginParametersImpl;
import ljas.commons.application.client.ClientApplication;
import ljas.commons.application.client.ClientApplicationAdapter;
import ljas.commons.client.Client;
import ljas.commons.client.ClientImpl;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.exceptions.SessionException;

public class ServerTestCase extends TestCase {
	public static final String APPLICATION_IDENTIFIER = "ljas.testing";
	public static final String APPLICATION_VERSION = "1.0";

	public static Client createAndConnectClient()
			throws ConnectionRefusedException, SessionException {
		return createAndConnectClient(APPLICATION_IDENTIFIER,
				APPLICATION_VERSION);
	}

	public static void connectClient(Client client)
			throws ConnectionRefusedException, SessionException {
		LoginParameters parameters = new LoginParametersImpl(
				client.getApplication());
		client.connect("localhost", 1666, parameters);
	}

	public static Client createClient() throws SessionException,
			ConnectionRefusedException {
		return createClient(APPLICATION_IDENTIFIER, APPLICATION_VERSION);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ServerManager.startupServer();
		assertTrue("Server is not online!", ServerManager.getServer()
				.isOnline());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		ServerManager.shutdownServer();
		assertFalse("Server is still online!", ServerManager.getServer()
				.isOnline());
	}

	protected static Client createClient(String applicationIdentifier,
			String applicationVersion) throws SessionException,
			ConnectionRefusedException {
		ClientApplication application = new ClientApplicationAdapter(
				applicationIdentifier, applicationVersion);

		Client client = new ClientImpl(application);
		return client;
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
