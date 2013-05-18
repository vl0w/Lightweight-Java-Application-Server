package ljas.client;

import ljas.application.LoginParameters;
import ljas.session.Address;
import ljas.session.Session;
import ljas.state.login.LoginMessage;
import ljas.threading.ThreadBlocker;

public class ClientLoginHandler extends ThreadBlocker<LoginMessage> {
	private LoginParameters loginParameters;
	private Session serverSession;
	private Address address;

	/**
	 * TODO too much parameters
	 * 
	 * @param ip
	 * @param port
	 * @param serverSession
	 * @param loginParameters
	 */
	public ClientLoginHandler(Address address, Session serverSession,
			LoginParameters loginParameters) {
		super(Client.REQUEST_TIMEOUT_MS);
		this.address = address;
		this.serverSession = serverSession;
		this.loginParameters = loginParameters;
	}

	@Override
	public LoginMessage block() throws Exception {
		ClientLoginSessionObserver loginObserver = new ClientLoginSessionObserver(
				this);
		serverSession.setObserver(loginObserver);
		serverSession.connect(address);
		serverSession.sendObject(loginParameters);
		return super.block();
	}

}
