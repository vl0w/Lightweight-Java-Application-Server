package ljas.client;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import ljas.state.login.LoginMessage;
import ljas.state.login.LoginRefusedMessage;

import org.junit.Test;

public class ClientLoginSessionDataObserverTest {
	@Test
	public void onObjectReceived() {
		ClientLoginHandler loginHandler = mock(ClientLoginHandler.class);
		LoginMessage loginMessage = mock(LoginMessage.class);

		ClientLoginSessionDataObserver observer = new ClientLoginSessionDataObserver(
				loginHandler);
		observer.onObjectReceived(null, loginMessage);

		verify(loginHandler).release(loginMessage);
	}

	@Test
	public void onObjectReceived_UnknownObjectReceived_LoginRefusedMessage() {
		ClientLoginHandler loginHandler = mock(ClientLoginHandler.class);

		ClientLoginSessionDataObserver observer = new ClientLoginSessionDataObserver(
				loginHandler);
		observer.onObjectReceived(null, new Object());

		verify(loginHandler).release(any(LoginRefusedMessage.class));
	}
}
