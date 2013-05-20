package ljas.client;

import ljas.session.Session;
import ljas.session.SessionObserver;
import ljas.state.login.LoginMessage;
import ljas.state.login.LoginRefusedMessage;

public class ClientLoginSessionObserver implements SessionObserver {

	private ClientLoginHandler clientLoginHandler;

	public ClientLoginSessionObserver(ClientLoginHandler clientLoginHandler) {
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

	@Override
	public void onSessionDisconnected(Session session) {
		// TODO Auto-generated method stub

	}

}
