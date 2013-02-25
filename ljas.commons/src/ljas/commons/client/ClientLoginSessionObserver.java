package ljas.commons.client;

import ljas.commons.session.Session;
import ljas.commons.session.SessionObserver;
import ljas.commons.state.login.LoginMessage;
import ljas.commons.state.login.LoginRefusedMessage;

public class ClientLoginSessionObserver implements SessionObserver {

	private ClientLoginHandler clientLoginHandler;

	public ClientLoginSessionObserver(ClientLoginHandler clientLoginHandler) {
		this.clientLoginHandler = clientLoginHandler;
	}

	@Override
	public void notiyObjectReceived(Session session, Object obj) {
		LoginMessage message = null;

		if (obj instanceof LoginMessage) {
			message = (LoginMessage) obj;
		} else {
			message = new LoginRefusedMessage("Unknown server response");
		}

		clientLoginHandler.release(message);
	}

	@Override
	public void notifySessionDisconnected(Session session) {
		// TODO Auto-generated method stub

	}

}
