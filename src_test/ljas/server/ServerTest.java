package ljas.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import ljas.application.Application;
import ljas.application.LoginParameters;
import ljas.application.annotations.LJASApplication;
import ljas.exception.ConnectionRefusedException;
import ljas.server.configuration.Property;
import ljas.session.Session;
import ljas.state.SystemAvailabilityState;
import ljas.state.login.LoginRefusedMessage;

import org.junit.Test;

public class ServerTest {

	@Test
	public void testCheckClient_ServerIsNotOnline_ConnectionReRefused()
			throws IOException {
		Application application = mock(Application.class);
		Server server = new Server(application);

		LoginParameters parameters = new LoginParameters(application);

		try {
			server.checkClient(parameters);
			fail("Connection is expected to be refused");
		} catch (ConnectionRefusedException e) {
			assertEquals(LoginRefusedMessage.ILLEGAL_STATE,
					e.getRefusedMessage());
		}
	}

	@Test
	public void testCheckClient_ServerFull_ConnectionRefused()
			throws IOException {
		Application application = mock(Application.class);

		LoginParameters parameters = new LoginParameters(application);

		Server server = new Server(application);
		server.getProperties().set(Property.MAXIMUM_CLIENTS, 2);
		server.setState(SystemAvailabilityState.ONLINE);

		server.addSession(mock(Session.class));
		server.addSession(mock(Session.class));
		try {
			server.checkClient(parameters);
			fail("Connection is expected to be refused");
		} catch (ConnectionRefusedException e) {
			assertEquals(LoginRefusedMessage.SERVER_FULL, e.getRefusedMessage());
		}
	}

	@Test
	public void testCheckClient_ApplicationsNotEqual_ConnectionRefused()
			throws IOException {
		Application application = mock(App1.class);
		LoginParameters parameters = new LoginParameters(mock(App2.class));

		Server server = new Server(application);
		server.getProperties().set(Property.MAXIMUM_CLIENTS, 2);
		server.setState(SystemAvailabilityState.ONLINE);

		try {
			server.checkClient(parameters);
			fail("Connection is expected to be refused");
		} catch (ConnectionRefusedException e) {
			assertEquals(LoginRefusedMessage.INVALID_APPLICATION,
					e.getRefusedMessage());
		}
	}

	@LJASApplication(name = "App1", version = "1.0")
	private interface App1 extends Application {

	}

	@LJASApplication(name = "App2", version = "1.0")
	private interface App2 extends Application {

	}

}
