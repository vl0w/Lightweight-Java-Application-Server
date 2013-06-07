package ljas.server.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import ljas.application.Application;
import ljas.application.LoginParameters;
import ljas.application.annotations.LJASApplication;
import ljas.exception.ConnectionRefusedException;
import ljas.exception.DisconnectException;
import ljas.server.Server;
import ljas.server.configuration.Property;
import ljas.server.socket.ServerSocketBinder;
import ljas.session.Session;
import ljas.state.login.LoginRefusedMessage;
import ljas.tasking.environment.TaskSystem;

import org.junit.Test;

public class OnlineStateTest {
	@Test
	public void testCheckClient_ServerFull_ConnectionRefused()
			throws IOException {
		Application application = mock(Application.class);
		LoginParameters parameters = new LoginParameters(application);
		Server server = new Server(application);
		server.getProperties().set(Property.MAXIMUM_CLIENTS, 2);

		server.addSession(mock(Session.class));
		server.addSession(mock(Session.class));

		OnlineState onlineState = new OnlineState(server);

		try {
			onlineState.checkClient(parameters);
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

		OnlineState onlineState = new OnlineState(server);

		try {
			onlineState.checkClient(parameters);
			fail("Connection is expected to be refused");
		} catch (ConnectionRefusedException e) {
			assertEquals(LoginRefusedMessage.INVALID_APPLICATION,
					e.getRefusedMessage());
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testStartup() {
		OnlineState onlineState = new OnlineState(null);
		onlineState.startup();
	}

	@Test
	public void testShutdown() throws IOException, DisconnectException {
		ServerSocketBinder binder = mock(ServerSocketBinder.class);
		TaskSystem taskSystem = mock(TaskSystem.class);
		Session session1 = mock(Session.class);
		Session session2 = mock(Session.class);

		Server server = new Server(mock(App1.class));
		server.setServerSocketBinder(binder);
		server.setTaskSystem(taskSystem);
		server.addSession(session1);
		server.addSession(session2);

		OnlineState onlineState = new OnlineState(server);
		onlineState.shutdown();

		verify(binder).close();
		verify(taskSystem).shutdown();
		verify(session1).disconnect();
		verify(session2).disconnect();
	}

	@LJASApplication(name = "App1", version = "1.0")
	private interface App1 extends Application {

	}

	@LJASApplication(name = "App2", version = "1.0")
	private interface App2 extends Application {

	}
}
