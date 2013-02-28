package ljas.commons.session.socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ljas.commons.session.SessionObserver;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.threading.ThreadSystem;

import org.junit.Test;

public class SocketSessionInputListenerTest {

	@Test
	public void testRunCycle_ObjectReceived_NotifyObservers() throws Exception {
		// Initialization
		String expectedObject = "SomeObject";

		// Mocking & Stubbing
		SocketSession session = mock(SocketSession.class);
		Socket socket = mock(Socket.class);
		SessionObserver observer = mock(SessionObserver.class);

		when(session.getSocket()).thenReturn(socket);
		when(socket.getInputStream()).thenReturn(
				new FakedInputStream(expectedObject));
		when(session.getObserver()).thenReturn(observer);

		// Run
		ThreadSystem threadSystem = new ThreadSystem(new TaskMonitor(), 0);
		SocketSessionInputListener listener = new SocketSessionInputListener(
				threadSystem);
		listener.setSession(session);
		listener.runCycle();

		// Verifications
		verify(observer).notiyObjectReceived(session, expectedObject);
	}

	@Test
	public void testRunCycle_IOExceptionOccurs_CorrectErrorHandlingOccurs()
			throws Exception {
		// Mocking & Stubbing
		SocketSession session = mock(SocketSession.class);
		Socket socket = mock(Socket.class);
		Runnable errorHandler = mock(Runnable.class);

		when(session.getSocket()).thenReturn(socket);
		when(socket.getInputStream()).thenThrow(new IOException());

		// Run
		ThreadSystem threadSystem = new ThreadSystem(new TaskMonitor(), 0);
		SocketSessionInputListener listener = new SocketSessionInputListener(
				threadSystem);
		listener.setSession(session);
		listener.setErrorHandler(errorHandler);
		listener.runCycle();

		// Verifications
		verify(errorHandler).run();
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
