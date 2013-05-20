package ljas.server.socket;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import org.junit.After;
import org.junit.Test;

public class ServerSocketBinderTest {

	private ServerSocketBinder binder = new ServerSocketBinder();

	@After
	public void unbindAll() throws Exception {
		binder.close();
	}

	@Test
	public void testBind_MultipleBindings() throws IOException {
		LoginObserver observer1 = mock(LoginObserver.class);
		LoginObserver observer2 = mock(LoginObserver.class);

		binder.bind(7000, observer1);

		binder.bind(7001, observer2);

		try (Socket s1 = new Socket("127.0.0.1", 7000);
				Socket s2 = new Socket("127.0.0.1", 7001)) {
			sleepSilent(5);
			verify(observer1).onSocketConnect(any(Socket.class));
			verify(observer2).onSocketConnect(any(Socket.class));
		}
	}

	@Test(expected = IOException.class)
	public void testBind_SameBindingAlreadyExists() throws IOException {
		binder.bind(7000, mock(LoginObserver.class));
		binder.bind(7000, mock(LoginObserver.class));
	}

	@Test(expected = ConnectException.class)
	public void testClose() throws IOException {
		binder.bind(7000, mock(LoginObserver.class));
		binder.close();
		try (Socket s1 = new Socket("localhost", 7000)) {

		}
	}

	private void sleepSilent(long time) {
		try {
			Thread.currentThread();
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
