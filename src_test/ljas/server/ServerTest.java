package ljas.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import ljas.commons.application.Application;
import ljas.commons.application.LoginParameters;
import ljas.commons.application.annotations.LJASApplication;
import ljas.commons.exceptions.ApplicationException;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.session.Session;
import ljas.commons.state.SystemAvailabilityState;
import ljas.commons.state.login.LoginRefusedMessage;
import ljas.server.configuration.Property;

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

	@Test(expected = ApplicationException.class)
	public void testStartup_ApplicationHasNoAnnotation_ThrowException()
			throws Exception {
		Server server = new Server(mock(AppWithNoAnnotation.class));
		try {
			server.startup();
		} finally {
			server.shutdown();
		}
	}

	@Test
	public void testStartup() throws IOException, ApplicationException {
		Server server = new Server(mock(App1.class));
		try {
			server.startup();

			assertNotNull(server.getServerSocket());
			assertFalse(server.getClientConnectionListenerService()
					.isShutdown());
			assertEquals(SystemAvailabilityState.ONLINE, server.getState());
		} finally {
			server.shutdown();
		}
	}

	@Test
	public void testShutdown() throws Exception {
		Server server = new Server(mock(App1.class));

		Session session1 = mock(Session.class);
		Session session2 = mock(Session.class);
		server.addSession(session1);
		server.addSession(session2);

		server.startup();
		server.shutdown();

		assertTrue(server.getServerSocket().isClosed());
		verify(session1).disconnect();
		verify(session2).disconnect();
		assertTrue(server.getClientConnectionListenerService().isShutdown());
		assertEquals(SystemAvailabilityState.OFFLINE, server.getState());
	}

	@LJASApplication(name = "App1", version = "1.0")
	private interface App1 extends Application {

	}

	@LJASApplication(name = "App2", version = "1.0")
	private interface App2 extends Application {

	}

	private interface AppWithNoAnnotation extends Application {

	}

}
