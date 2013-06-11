package ljas.server;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import ljas.application.Application;
import ljas.exception.ApplicationException;
import ljas.session.Session;

import org.junit.Test;

public class ServerDisconnectSessionObserverTest {

	@Test
	public void testOnSessionDisconnect() throws IOException,
			ApplicationException {
		Application serverApplication = mock(Application.class);
		Server server = new Server(serverApplication);
		Session session = mock(Session.class);

		server.addSession(session);

		ServerDisconnectSessionObserver observer = new ServerDisconnectSessionObserver(
				server);
		observer.onSessionDisconnected(session);

		verify(serverApplication).onSessionDisconnect(session);
		assertTrue(server.getSessions().isEmpty());
	}

}
