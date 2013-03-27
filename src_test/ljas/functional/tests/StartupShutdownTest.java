package ljas.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import ljas.client.Client;
import ljas.commons.exceptions.SessionException;
import ljas.functional.ServerManager;
import ljas.functional.ServerTestCase;
import ljas.functional.tasks.AdditionTask;
import ljas.server.Server;

import org.junit.Test;

public class StartupShutdownTest extends ServerTestCase {

	@Test(timeout = TEST_TIMEOUT)
	public void testSingleStartupShutdown_CheckConnectedClients()
			throws Exception {
		Server server = ServerManager.getServer();

		createAndConnectClient();
		createAndConnectClient();
		createAndConnectClient();

		server.shutdown();
		assertServerOffline();
	}

	@Test(timeout = TEST_TIMEOUT)
	public void testMultipleStartupShutdown_CheckInteractions()
			throws Exception {
		Server server = ServerManager.getServer();

		server.startup();
		assertServerOnline();

		server.shutdown();
		assertServerOffline();

		server.startup();
		assertServerOnline();

		server.shutdown();
		assertServerOffline();
	}

	private void assertServerOnline() throws Exception {
		Server server = ServerManager.getServer();
		assertTrue(server.isOnline());
		assertFalse(server.getServerSocket().isClosed());

		assertClientInteractionsPossible();
	}

	private void assertServerOffline() throws Exception {
		Server server = ServerManager.getServer();
		assertFalse(server.isOnline());
		assertTrue(server.getServerSocket().isClosed());
		assertTrue(server.getSessions().isEmpty());
		assertClientInteractionsImpossible();
	}

	private void assertClientInteractionsPossible() throws Exception,
			SessionException {
		Client client = createAndConnectClient();
		AdditionTask executedTask = client.runTaskSync(new AdditionTask(client,
				5, 5));
		assertEquals(10, executedTask.sum);
		client.disconnect();
	}

	private void assertClientInteractionsImpossible() {
		Client client = null;
		try {
			client = createAndConnectClient();
			AdditionTask executedTask = client.runTaskSync(new AdditionTask(
					client, 5, 5));
			assertEquals(10, executedTask.sum);
			fail("No interactions between client and server are expected");
		} catch (Exception e) {
			// good
		}
	}
}
