package ljas.commons.application.server;

import ljas.commons.application.LoginParameters;
import ljas.commons.session.Session;

public class NullServerApplication extends ServerApplication {

	public NullServerApplication(String name, String version) {
		super(name, version);
	}

	@Override
	public void start() {
		// nothing
	}

	@Override
	public void registerUser(Session session, LoginParameters parameter)
			throws ServerApplicationException {
		// nothing
	}

	@Override
	public void removeUser(Session session) throws ServerApplicationException {
		// nothing
	}

}
