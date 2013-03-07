package ljas.commons.application.server;

import ljas.commons.application.LoginParameters;
import ljas.commons.session.Session;

public class NullServerApplication extends ServerApplication {

	public NullServerApplication(String name, String version) {
		super(name, version);
	}

	@Override
	public void onSessionConnect(Session session, LoginParameters parameter)
			throws ServerApplicationException {
		// nothing
	}

	@Override
	public void onSessionDisconnect(Session session) throws ServerApplicationException {
		// nothing
	}

}
