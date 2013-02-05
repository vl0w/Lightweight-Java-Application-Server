package ljas.testing.tests;

import java.io.IOException;
import java.net.ConnectException;

import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.state.RefusedMessage;
import ljas.testing.ServerTestCase;

public class ConnectionTest extends ServerTestCase {
	public void testConnectionOk() {
		try {
			createAndConnectClient();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testWrongApplicationId() {
		try {
			createAndConnectClient("com.maps.haxxor",ServerTestCase.APPLICATION_VERSION);
			fail("Should throw exception.");
		} catch (ConnectException e) {
			fail("Can not connect to server");
		} catch (ConnectionRefusedException e) {
			assertEquals(RefusedMessage.WRONG_APPLICATION_ID,
					e.getRefusedMessage());
		}
	}

	public void testWrongVersion() {
		try {
			createAndConnectClient(ServerTestCase.APPLICATION_IDENTIFIER, "0.5");
			fail("Should throw exception.");
		} catch (ConnectException e) {
			fail("Can not connect to server");
		} catch (ConnectionRefusedException e) {
			assertEquals(RefusedMessage.WRONG_APPLICATION_VERSION,
					e.getRefusedMessage());
		}
	}

	public void testServerFull() throws IOException, ConnectionRefusedException{
		createClients();

		try{
			createAndConnectClient();
			fail("Should throw an exception");
		}catch(ConnectionRefusedException e){
			// Success!
			assertEquals(RefusedMessage.SERVER_FULL, e.getRefusedMessage());
		}

	}
}
