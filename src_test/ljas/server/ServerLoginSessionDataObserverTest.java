package ljas.server;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ljas.application.Application;
import ljas.application.LoginParameters;
import ljas.exception.ApplicationException;
import ljas.exception.ConnectionRefusedException;
import ljas.exception.SessionException;
import ljas.session.Session;
import ljas.state.login.LoginAcceptedMessage;
import ljas.state.login.LoginRefusedMessage;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class ServerLoginSessionDataObserverTest {

	private Server server;
	private Application serverApplication;

	@Before
	public void setupServerAndApplication() {
		server = mock(Server.class);
		serverApplication = mock(Application.class);
		when(server.getLogger()).thenReturn(Logger.getLogger(getClass()));
		when(server.getApplication()).thenReturn(serverApplication);
	}

	@Test
	public void testOnObjectReceived() throws SessionException,
			ApplicationException, ConnectionRefusedException {
		Session session = mock(Session.class);
		LoginParameters loginParameters = mock(LoginParameters.class);

		ServerLoginSessionDataObserver observer = new ServerLoginSessionDataObserver(
				server);
		observer.onObjectReceived(session, loginParameters);

		// Verify calls on server
		verify(server).checkClient(loginParameters);
		verify(server).addSession(session);
		verify(serverApplication).onSessionConnect(session, loginParameters);

		// Verify calls on session
		verify(session).sendObject(any(LoginAcceptedMessage.class));
		verify(session).setDisconnectObserver(
				any(ServerDisconnectSessionObserver.class));
	}

	@Test
	public void testOnObjectReceived_UnknownObjectByClient()
			throws SessionException, ApplicationException,
			ConnectionRefusedException {
		Session session = mock(Session.class);
		Object object = new Object();

		ServerLoginSessionDataObserver observer = new ServerLoginSessionDataObserver(
				server);
		observer.onObjectReceived(session, object);

		verify(session).sendObject(
				LoginRefusedMessage.UNKNOWN_EXCEPTION_OCCURED);
	}

	@Test
	public void testOnObjectReceived_ServerRefusesLogin()
			throws SessionException, ApplicationException,
			ConnectionRefusedException {
		Session session = mock(Session.class);
		LoginParameters loginParameters = mock(LoginParameters.class);

		ConnectionRefusedException toBeThrown = new ConnectionRefusedException(
				LoginRefusedMessage.SERVER_FULL);
		doThrow(toBeThrown).when(server).checkClient(loginParameters);

		ServerLoginSessionDataObserver observer = new ServerLoginSessionDataObserver(
				server);
		observer.onObjectReceived(session, loginParameters);

		verify(session).sendObject(LoginRefusedMessage.SERVER_FULL);
	}

	@Test
	public void testOnObjectReceived_ApplicationRefusesLogin()
			throws SessionException, ApplicationException,
			ConnectionRefusedException {
		Session session = mock(Session.class);
		LoginParameters loginParameters = mock(LoginParameters.class);

		doThrow(ApplicationException.class).when(serverApplication)
				.onSessionConnect(session, loginParameters);

		ServerLoginSessionDataObserver observer = new ServerLoginSessionDataObserver(
				server);
		observer.onObjectReceived(session, loginParameters);

		verify(session).sendObject(
				LoginRefusedMessage.UNKNOWN_EXCEPTION_OCCURED);
	}
}
