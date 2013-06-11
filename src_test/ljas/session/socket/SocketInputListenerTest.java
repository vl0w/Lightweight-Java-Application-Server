package ljas.session.socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ljas.session.observer.SessionDataObserver;

import org.junit.Test;

public class SocketInputListenerTest {

	@Test
	public void testRunCycle_ObjectReceived_NotifyObservers() throws Exception {
		// Initialization
		String expectedObject = "SomeObject";

		// Mocking & Stubbing
		SocketSession session = mock(SocketSession.class);
		Socket socket = mock(Socket.class);
		SessionDataObserver observer = mock(SessionDataObserver.class);

		when(session.getSocket()).thenReturn(socket);
		when(socket.getInputStream()).thenReturn(
				new FakedInputStream(expectedObject));
		when(session.getDataObserver()).thenReturn(observer);

		// Run
		SocketInputListener listener = new SocketInputListener();
		listener.setSession(session);
		listener.runCycle();

		// Verifications
		verify(observer).onObjectReceived(session, expectedObject);

	}

	@Test
	public void testRunCycle_IOExceptionOccurs_SessionGetsDisconnected()
			throws Exception {
		// Mocking & Stubbing
		SocketSession session = mock(SocketSession.class);
		Socket socket = mock(Socket.class);

		when(session.getSocket()).thenReturn(socket);
		when(socket.getInputStream()).thenThrow(new IOException());

		// Run
		SocketInputListener listener = new SocketInputListener();
		listener.setSession(session);
		listener.setDisconnectable(session);
		listener.runCycle();

		// Verifications
		verify(session).disconnect();
	}

	private class FakedInputStream extends InputStream {

		private byte[] byteArray;
		private int index;

		public FakedInputStream(Object expectedObj) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(out);
			outputStream.writeObject(expectedObj);
			byteArray = out.toByteArray();
			index = -1;
		}

		@Override
		public int read() throws IOException {
			return byteArray[++index];
		}

	}
}
