package ljas.testing.tests;

import ljas.commons.client.Client;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.state.login.LoginRefusedMessage;
import ljas.testing.ServerTestCase;

import org.junit.Test;

public class ConnectionTest extends ServerTestCase {

	@Test
	public void testOneConnection() throws Exception {
		Client client = createAndConnectClient();
		assertTrue(client.isOnline());
	}

	@Test
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
