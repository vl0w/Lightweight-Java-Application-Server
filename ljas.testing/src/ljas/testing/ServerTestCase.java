package ljas.testing;

import java.io.IOException;
import java.net.ConnectException;

import junit.framework.TestCase;
import ljas.commons.application.LoginParameters;
import ljas.commons.application.LoginParametersImpl;
import ljas.commons.application.client.ClientApplication;
import ljas.commons.application.client.ClientApplicationAdapter;
import ljas.commons.application.server.ServerApplicationAdapter;
import ljas.commons.client.Client;
import ljas.commons.client.ClientImpl;
import ljas.commons.client.ClientUI;
import ljas.commons.client.EmptyUI;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.server.Server;

public class ServerTestCase extends TestCase {
	public static final String APPLICATION_IDENTIFIER = "ljas.testing";
	public static final String APPLICATION_VERSION = "1.0";

	private Server _server;

	private void setServer(Server server) {
		_server = server;
	}

	protected Server getServer() {
		return _server;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Server server = createServer();
		server.startup();
		assertTrue("Server is not online!", server.isOnline());
		setServer(server);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		Server server = getServer();
		server.shutdown();
		assertFalse("Server is still online!", server.isOnline());
	}

	private Server createServer() throws IOException {
		return new Server(new ServerApplicationAdapter(APPLICATION_IDENTIFIER,
				APPLICATION_VERSION),
				"./configuration/ServerConfiguration.properties");
	}

	public static Client createClient() throws ConnectException,
			ConnectionRefusedException {
		return createClient(APPLICATION_IDENTIFIER, APPLICATION_VERSION);
	}

	protected static Client createClient(String applicationIdentifier,
			String applicationVersion) throws ConnectException,
			ConnectionRefusedException {
		ClientApplication application = new ClientApplicationAdapter(
				applicationIdentifier, applicationVersion);
		ClientUI ui = new EmptyUI();

		Client client = new ClientImpl(ui, application);
		return client;
	}

	protected static Client createAndConnectClient(
			String applicationIdentifier, String applicationVersion)
			throws ConnectException, ConnectionRefusedException {
		Client client = createClient();
		connectClient(client);
		return client;
	}

	public static Client createAndConnectClient() throws ConnectException,
			ConnectionRefusedException {
		return createAndConnectClient(APPLICATION_IDENTIFIER,
				APPLICATION_VERSION);
	}

	public static void connectClient(Client client) throws ConnectException,
			ConnectionRefusedException {
		LoginParameters parameters = new LoginParametersImpl(
				client.getApplication());
		client.connect("localhost", 1666, parameters);
	}

	protected Client[] createClients() throws ConnectionRefusedException,
			IOException {
		int maximumClients = ServerManager.getServer().getConfiguration()
				.getMaximumClients();
		return createClients(maximumClients);
	}

	protected Client[] createClients(int amount)
			throws ConnectionRefusedException, IOException {
		Client[] clients = new Client[amount];

		// Connect maximum amount of clients
		for (int i = 0; i < amount; i++) {
			clients[i] = createAndConnectClient();
		}

		return clients;
	}

}
