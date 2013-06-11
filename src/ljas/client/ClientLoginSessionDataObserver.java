package ljas.client;

import ljas.session.Session;
import ljas.session.observer.SessionDataObserver;
import ljas.state.login.LoginMessage;
import ljas.state.login.LoginRefusedMessage;

public class ClientLoginSessionDataObserver implements SessionDataObserver {

	private ClientLoginHandler clientLoginHandler;

	public ClientLoginSessionDataObserver(ClientLoginHandler clientLoginHandler) {
		this.clientLoginHandler = clientLoginHandler;
	}

	@Override
	public void onObjectReceived(Session session, Object obj) {
		LoginMessage message = null;

		if (obj instanceof LoginMessage) {
			message = (LoginMessage) obj;
		} else {
			message = new LoginRefusedMessage("Unknown server response");
		}

		clientLoginHandler.release(message);
	}
}
