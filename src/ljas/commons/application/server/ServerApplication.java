package ljas.commons.application.server;

import ljas.commons.application.Application;
import ljas.commons.application.LoginParameters;
import ljas.commons.session.Session;

public abstract class ServerApplication extends Application {
	public ServerApplication(String name, String version) {
		super(name, version);
	}

	/**
	 * Gets called, when a user connects to the server
	 * 
	 * @param connection
	 *            The connection to the user
	 * @param parameters
	 *            The login parameters
	 * @throws ServerApplicationException
	 */
	public abstract void registerUser(Session session,
			LoginParameters parameters) throws ServerApplicationException;

	/**
	 * Gets called, when a user closed the server connection or the server lost
	 * the connection
	 * 
	 * @param connection
	 *            The connection of the user
	 * @throws ServerApplicationException
	 */
	public abstract void removeUser(Session session)
			throws ServerApplicationException;
}
