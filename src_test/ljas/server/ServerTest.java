package ljas.server;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import ljas.application.Application;
import ljas.application.LoginParameters;
import ljas.exception.ConnectionRefusedException;
import ljas.server.state.ServerState;

import org.junit.Test;

public class ServerTest {
	@Test
	public void testShutdown() throws IOException {
		ServerState serverState = mock(ServerState.class);

		Server server = new Server(mock(Application.class));
		server.setCurrentState(serverState);

		server.shutdown();

		verify(serverState).shutdown();
		assertTrue(server.isOffline());
	}

	@Test
	public void testCheckClient() throws IOException,
			ConnectionRefusedException {
		ServerState serverState = mock(ServerState.class);
		LoginParameters loginParameters = mock(LoginParameters.class);

		Server server = new Server(mock(Application.class));
		server.setCurrentState(serverState);

		server.checkClient(loginParameters);

		verify(serverState).checkClient(loginParameters);
	}
}
