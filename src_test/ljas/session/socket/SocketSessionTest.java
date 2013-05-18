package ljas.session.socket;

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

import ljas.exception.SessionException;
import ljas.session.Address;
import ljas.session.SessionObserver;

import org.junit.Test;

public class SocketSessionTest {

	@Test
	public void testDisconnect() throws IOException {
		// Mocking & Stubbing
		Socket socket = mock(Socket.class);
		SessionObserver sessionObserver = mock(SessionObserver.class);

		// Run
		SocketSession session = createSession(socket, sessionObserver);
		session.disconnect();

		// Verifications
		verify(socket).close();
		verify(sessionObserver).notifySessionDisconnected(session);

		// Asserts
		assertFalse(session.isConnected());
		assertNull(session.getSocket());
		assertFalse(session.getInputListenerThread().isAlive());
	}

	@Test
	public void testConnect_NotConnected_Connect() throws SessionException,
			UnknownHostException, IOException {
		// Initialization
		Address address = Address.parseFromString("localhost:1337");

		// Mocking & Stubbing
		Socket socket = mock(Socket.class);
		SocketFactory socketFactory = mock(SocketFactory.class);
		SessionObserver sessionObserver = mock(SessionObserver.class);

		when(socketFactory.createSocket(address)).thenReturn(socket);

		// Run
		SocketSession session = createSession(sessionObserver);
		session.setSocketFactory(socketFactory);
		session.connect(address);

		// Verifications
		verify(socketFactory).createSocket(address);

		// Asserts
		assertTrue(session.isConnected());
		assertNotNull(session.getSocket());
	}

	@Test
	public void testConnect_AlreadyConnected_DisconnectAndConnect()
			throws SessionException, UnknownHostException, IOException {
		// Initialization
		Address address = Address.parseFromString("localhost:1337");

		// Mocking & Stubbing
		Socket socket = mock(Socket.class);
		SocketFactory socketFactory = mock(SocketFactory.class);
		SessionObserver sessionObserver = mock(SessionObserver.class);

		when(socketFactory.createSocket(address)).thenReturn(socket);

		// Run
		SocketSession session = createSession(socket, sessionObserver);
		session.setSocketFactory(socketFactory);
		session.connect(address);

		// Verifications: Disconnected
		verify(socket).close();
		verify(sessionObserver).notifySessionDisconnected(session);

		// Verifications: Connected
		verify(socketFactory).createSocket(address);

		// Asserts
		assertTrue(session.isConnected());
		assertNotNull(session.getSocket());
	}

	private SocketSession createSession(Socket socket,

	SessionObserver observer) {
		SocketSession socketSession = new SocketSession(socket);
		socketSession.setObserver(observer);
		return socketSession;
	}

	private SocketSession createSession(SessionObserver observer) {
		SocketSession socketSession = new SocketSession();
		socketSession.setObserver(observer);
		return socketSession;
	}

}
