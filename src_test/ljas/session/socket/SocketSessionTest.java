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
import ljas.session.observer.SessionDisconnectObserver;

import org.junit.Test;

public class SocketSessionTest {

	@Test
	public void testDisconnect() throws IOException {
		// Mocking & Stubbing
		Socket socket = mock(Socket.class);
		SessionDisconnectObserver observer = mock(SessionDisconnectObserver.class);

		// Run
		SocketSession session = new SocketSession(socket);
		session.setDisconnectObserver(observer);
		session.disconnect();

		// Verifications
		verify(socket).close();
		verify(observer).onSessionDisconnected(session);

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

		when(socketFactory.createSocket(address)).thenReturn(socket);

		// Run
		SocketSession session = new SocketSession(socket);
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
		SessionDisconnectObserver observer = mock(SessionDisconnectObserver.class);

		when(socketFactory.createSocket(address)).thenReturn(socket);

		// Run
		SocketSession session = new SocketSession(socket);
		session.setDisconnectObserver(observer);
		session.setSocketFactory(socketFactory);
		session.connect(address);

		// Verifications: Disconnected
		verify(socket).close();
		verify(observer).onSessionDisconnected(session);

		// Verifications: Connected
		verify(socketFactory).createSocket(address);

		// Asserts
		assertTrue(session.isConnected());
		assertNotNull(session.getSocket());
	}

}
