package ljas.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import ljas.client.Client;
import ljas.commons.exceptions.DisconnectException;
import ljas.commons.exceptions.SessionException;
import ljas.functional.ServerManager;
import ljas.functional.ServerTestCase;
import ljas.functional.tasks.AdditionTask;
import ljas.server.Server;

import org.junit.Test;

public class StartupShutdownTest extends ServerTestCase {

	@Test(timeout = TEST_TIMEOUT)
	public void startupShutdown() throws Exception {
		Server server = ServerManager.getServer();

		server.startup();
		assertTrue(server.isOnline());
		assertClientInteractions();

		server.shutdown();
		assertFalse(server.isOnline());
		assertNoClientInteractions();

		server.startup();
		assertTrue(server.isOnline());
		assertClientInteractions();

		server.shutdown();
		assertFalse(server.isOnline());
		assertNoClientInteractions();
	}

	private void assertClientInteractions() throws Exception, SessionException {
		Client client = createAndConnectClient();
		AdditionTask executedTask = (AdditionTask) client
				.runTaskSync(new AdditionTask(client, 5, 5));
		assertEquals(10, executedTask.sum, 0.00001);
		client.disconnect();
	}

	private void assertNoClientInteractions() throws DisconnectException {
		Client client = null;
		try {
			client = createAndConnectClient();
			AdditionTask executedTask = (AdditionTask) client
					.runTaskSync(new AdditionTask(client, 5, 5));
			assertEquals(10, executedTask.sum, 0.00001);
			fail("No interactions between client and server are expected");
		} catch (Exception e) {
			// good
		}
	}
}
