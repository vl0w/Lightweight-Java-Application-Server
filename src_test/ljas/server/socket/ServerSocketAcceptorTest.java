package ljas.server.socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

public class ServerSocketAcceptorTest {

	@Test
	public void testRunCycle_ObserverGetsNotified() throws IOException {
		LoginObserver observer = mock(LoginObserver.class);
		ServerSocket serverSocket = mock(ServerSocket.class);
		Socket socket = mock(Socket.class);

		when(serverSocket.accept()).thenReturn(socket);

		ServerSocketAcceptor acceptor = new ServerSocketAcceptor(serverSocket,
				observer);
		acceptor.runCycle();

		verify(observer).onSocketConnect(socket);
	}

}
