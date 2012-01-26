package ljas.testing.tests;

import java.net.ConnectException;
import ljas.commons.client.Client;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.state.RefusedMessage;
import ljas.testing.ServerTestCase;

public class ConnectionTest extends ServerTestCase {
	public void testConnectionOk() {
		Client client=null;
		try {
			client = createClient();
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			client.disconnect();
		}
	}

	public void testWrongApplicationId() {
		Client client = null;
		try {
			client=createClient("com.maps.haxxor",ServerTestCase.APPLICATION_VERSION);
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
		Client client = null;
		try {
			client = createClient(ServerTestCase.APPLICATION_ID, "0.5");
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
}
