package ljas.testing.tests;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ljas.commons.client.Client;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.state.RefusedMessage;

public class ConnectionTest extends TestCase {
	public void testConnectionOk() {
		Client client = Constants.createClient();
		try {
			Constants.doConnect(client);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			client.disconnect();
		}
	}

	public void testWrongApplicationId() {
		Client client = Constants.createClient("com.maps.haxxor",
				Constants.APPLICATION_VERSION);
		try {
			Constants.doConnect(client);
			fail("Should throw exception.");
		} catch (ConnectException e) {
			fail("Can not connect to server");
		} catch (ConnectionRefusedException e) {
			assertEquals(RefusedMessage.WRONG_APPLICATION_ID,
					e.getRefusedMessage());
		} finally {
			client.disconnect();
		}
	}

	public void testWrongVersion() {
		Client client = Constants.createClient(Constants.APPLICATION_ID, "0.5");
		try {
			Constants.doConnect(client);
			fail("Should throw exception.");
		} catch (ConnectException e) {
			fail("Can not connect to server");
		} catch (ConnectionRefusedException e) {
			assertEquals(RefusedMessage.WRONG_APPLICATION_VERSION,
					e.getRefusedMessage());
		} finally {
			client.disconnect();
		}
	}

	public void testServerFull() {
		List<Client> clientList = new ArrayList<Client>();
		try {
			int maxUsers = Constants.getServerConfiguration()
					.getMaximumClients();
			for (int i = 0; i < maxUsers; i++) {
				clientList.add(Constants.createClient());
			}

			// Fill server
			for (Client client : clientList) {
				try {
					Constants.doConnect(client);
				} catch (ConnectionRefusedException e) {
					fail(e.getMessage());
				}
			}

			// Another user, but server should be full
			Client failClient = Constants.createClient();

			try {
				Constants.doConnect(failClient);
			} catch (ConnectionRefusedException e) {
				assertEquals(RefusedMessage.SERVER_FULL, e.getRefusedMessage());
			} finally {
				failClient.disconnect();
			}

		} catch (IOException e) {
			fail(e.getMessage());
		} finally {
			for (Client client : clientList) {
				client.disconnect();
			}
		}
	}
}
