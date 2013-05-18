package ljas.integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import ljas.client.Client;
import ljas.exception.ConnectionRefusedException;
import ljas.integration.ServerTestCase;
import ljas.state.login.LoginRefusedMessage;
import ljas.testing.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ConnectionTest extends ServerTestCase {

	@Test(timeout = TEST_TIMEOUT)
	public void testOneConnection() throws Exception {
		Client client = createAndConnectClient();
		assertTrue(client.isOnline());
	}

	@Test(timeout = TEST_TIMEOUT)
	public void testServerFull() throws Exception {
		createClients();

		try {
			createAndConnectClient();
			fail("Should throw an exception");
		} catch (ConnectionRefusedException e) {
			// Success!
			assertEquals(LoginRefusedMessage.SERVER_FULL, e.getRefusedMessage());
		}

	}
}
