package ljas.client;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import ljas.application.LoginParameters;
import ljas.session.Address;
import ljas.session.Session;
import ljas.state.login.LoginMessage;
import ljas.threading.ThreadBlocker;

import org.junit.Test;

public class ClientLoginHandlerTest {
	@Test
	public void testBlock_verifyServerSession() throws Exception {
		Address address = mock(Address.class);
		Session serverSession = mock(Session.class);
		LoginParameters loginParameters = mock(LoginParameters.class);

		ClientLoginHandler loginHandler = new ClientLoginHandler(address,
				serverSession, loginParameters);
		new BlockerReleaser(loginHandler).start();
		loginHandler.block();

		verify(serverSession).setDataObserver(
				any(ClientLoginSessionDataObserver.class));
		verify(serverSession).connect(address);
		verify(serverSession).sendObject(loginParameters);
	}

	private class BlockerReleaser extends Thread {

		private ThreadBlocker<LoginMessage> blocker;

		public BlockerReleaser(ThreadBlocker<LoginMessage> blocker) {
			this.blocker = blocker;
		}

		@Override
		public void run() {
			try {
				Thread.currentThread();
				Thread.sleep(50);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			blocker.release(mock(LoginMessage.class));
		};

	}
}
