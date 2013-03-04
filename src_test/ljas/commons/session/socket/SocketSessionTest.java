package ljas.commons.session.socket;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ljas.commons.exceptions.SessionException;
import ljas.commons.session.SessionObserver;

import org.junit.Test;

public class SocketSessionTest {

	@Test
	public void testDisconnect() throws IOException {
		// Mocking & Stubbing
		Socket socket = mock(Socket.class);
		SocketSessionInputListener socketInputListener = mock(SocketSessionInputListener.class);
		SessionObserver sessionObserver = mock(SessionObserver.class);

		// Run
		SocketSession session = createSession(socket, socketInputListener,
				sessionObserver);
		session.disconnect();

		// Verifications
		verify(socket).close();
		verify(socketInputListener).kill();
		verify(sessionObserver).notifySessionDisconnected(session);

		// Asserts
		assertFalse(session.isConnected());
		assertNull(session.getSocket());
	}

	@Test
	public void testConnect_NotConnected_Connect() throws SessionException,
			UnknownHostException, IOException {
		// Initialization
		final String ip = "localhost";
		final int port = 1337;

		// Mocking & Stubbing
		Socket socket = mock(Socket.class);
		SimpleSocketFactory socketFactory = mock(SimpleSocketFactory.class);
		SocketSessionInputListener socketInputListener = mock(SocketSessionInputListener.class);
		SessionObserver sessionObserver = mock(SessionObserver.class);

		when(socketFactory.createSocket(ip, port)).thenReturn(socket);

		// Run
		SocketSession session = createSession(socketInputListener,
				sessionObserver);
		session.setSocketFactory(socketFactory);
		session.connect(ip, port);

		// Verifications
		verify(socketInputListener).start();
		verify(socketFactory).createSocket(ip, port);

		// Asserts
		assertTrue(session.isConnected());
		assertNotNull(session.getSocket());
	}

	@Test
	public void testConnect_AlreadyConnected_DisconnectAndConnect()
			throws SessionException, UnknownHostException, IOException {
		// Initialization
		final String ip = "localhost";
		final int port = 1337;

		// Mocking & Stubbing
		Socket socket = mock(Socket.class);
		SimpleSocketFactory socketFactory = mock(SimpleSocketFactory.class);
		SocketSessionInputListener socketInputListener = mock(SocketSessionInputListener.class);
		SessionObserver sessionObserver = mock(SessionObserver.class);

		when(socketFactory.createSocket(ip, port)).thenReturn(socket);

		// Run
		SocketSession session = createSession(socket, socketInputListener,
				sessionObserver);
		session.setSocketFactory(socketFactory);
		session.connect(ip, port);

		// Verifications: Disconnected
		verify(socket).close();
		verify(socketInputListener).kill();
		verify(sessionObserver).notifySessionDisconnected(session);

		// Verifications: Connected
		verify(socketInputListener).start();
		verify(socketFactory).createSocket(ip, port);

		// Asserts
		assertTrue(session.isConnected());
		assertNotNull(session.getSocket());
	}

	private SocketSession createSession(Socket socket,
			SocketSessionInputListener socketInputListener,
			SessionObserver observer) {
		SocketSession socketSession = new SocketSession(socketInputListener,
				socket);
		socketSession.setObserver(observer);
		return socketSession;
	}

	private SocketSession createSession(
			SocketSessionInputListener socketInputListener,
			SessionObserver observer) {
		SocketSession socketSession = new SocketSession(socketInputListener);
		socketSession.setObserver(observer);
		return socketSession;
	}

}
