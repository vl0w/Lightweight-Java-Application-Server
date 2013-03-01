package ljas.commons.client;

import ljas.commons.application.LoginParameters;
import ljas.commons.session.Session;
import ljas.commons.state.login.LoginMessage;
import ljas.commons.threading.ThreadBlocker;

public class ClientLoginHandler extends ThreadBlocker<LoginMessage> {
	private LoginParameters loginParameters;
	private Session serverSession;
	private String ip;
	private int port;

	/**
	 * TODO too much parameters
	 * 
	 * @param ip
	 * @param port
	 * @param serverSession
	 * @param loginParameters
	 */
	public ClientLoginHandler(String ip, int port, Session serverSession,
			LoginParameters loginParameters) {
		super(Client.REQUEST_TIMEOUT_MS);
		this.ip = ip;
		this.port = port;
		this.serverSession = serverSession;
		this.loginParameters = loginParameters;
	}

	@Override
	public LoginMessage block() throws Throwable {
		ClientLoginSessionObserver loginObserver = new ClientLoginSessionObserver(
				this);
		serverSession.setObserver(loginObserver);
		serverSession.connect(ip, port);
		serverSession.sendObject(loginParameters);
		return super.block();
	}

}
