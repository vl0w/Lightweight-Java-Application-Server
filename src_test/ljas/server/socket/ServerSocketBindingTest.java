package ljas.server.socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

import org.junit.Test;

public class ServerSocketBindingTest {

	@Test
	public void testClose() throws IOException {
		ServerSocket serverSocket = mock(ServerSocket.class);
		ExecutorService executorService = mock(ExecutorService.class);

		ServerSocketBinding binding = new ServerSocketBinding(serverSocket,
				executorService);
		binding.close();

		verify(serverSocket).close();
		verify(executorService).shutdownNow();
	}

}
