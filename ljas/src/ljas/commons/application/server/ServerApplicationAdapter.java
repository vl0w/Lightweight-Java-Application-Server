package ljas.commons.application.server;

import ljas.commons.application.LoginParameters;
import ljas.commons.session.Session;

/**
 * Provides a minimalistic standard-implementation
 * 
 * @author Jonas Hansen
 * 
 */
public class ServerApplicationAdapter extends ServerApplication {

	public ServerApplicationAdapter(String name, String version) {
		super(name, version);
	}

	@Override
	public void start() {
		// nothing
	}

	@Override
	public void registerUser(Session session, LoginParameters parameter)
			throws ServerApplicationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUser(Session session) throws ServerApplicationException {
		// TODO Auto-generated method stub

	}

}
