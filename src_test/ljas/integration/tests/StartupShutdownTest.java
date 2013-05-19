package ljas.integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import ljas.client.Client;
import ljas.exception.SessionException;
import ljas.integration.ServerTestCase;
import ljas.integration.tasks.AdditionTask;
import ljas.testing.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class StartupShutdownTest extends ServerTestCase {

	@Test(timeout = TEST_TIMEOUT)
	public void testSingleStartupShutdown_CheckConnectedClients()
			throws Exception {
		createAndConnectClient();
		createAndConnectClient();
		createAndConnectClient();

		getServer().shutdown();
		assertServerOffline();
	}

	@Test(timeout = TEST_TIMEOUT)
	public void testMultipleStartupShutdown_CheckInteractions()
			throws Exception {
		getServer().startup();
		assertServerOnline();

		getServer().shutdown();
		assertServerOffline();

		getServer().startup();
		assertServerOnline();

		getServer().shutdown();
		assertServerOffline();
	}

	private void assertServerOnline() throws Exception {
		assertTrue(getServer().isOnline());

		assertClientInteractionsPossible();
	}

	private void assertServerOffline() throws Exception {
		assertFalse(getServer().isOnline());
		assertTrue(getServer().getSessions().isEmpty());
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
