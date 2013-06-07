package ljas.server.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import ljas.application.Application;
import ljas.application.LoginParameters;
import ljas.exception.ApplicationException;
import ljas.exception.ConnectionRefusedException;
import ljas.state.login.LoginRefusedMessage;

import org.junit.Test;

public class OfflineStateTest {
	@Test
	public void testCheckClient_ConnectionRefused() throws IOException {
		Application application = mock(Application.class);
		LoginParameters parameters = new LoginParameters(application);

		OfflineState offlineState = new OfflineState();

		try {
			offlineState.checkClient(parameters);
			fail("Connection is expected to be refused");
		} catch (ConnectionRefusedException e) {
			assertEquals(LoginRefusedMessage.ILLEGAL_STATE,
					e.getRefusedMessage());
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testShutdown() {
		OfflineState offlineState = new OfflineState();
		offlineState.shutdown();
	}

	@Test(expected = IllegalStateException.class)
	public void testStartup() throws ApplicationException, IOException {
		OfflineState offlineState = new OfflineState();
		offlineState.startup();
	}
}
