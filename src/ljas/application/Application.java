package ljas.application;

import ljas.exception.ApplicationException;
import ljas.session.Session;

public interface Application {
	/**
	 * Gets called when a user connects to the server
	 * 
	 * @param connection
	 *            The connection to the user
	 * @param parameters
	 *            The login parameters
	 * @throws ApplicationException
	 */
	void onSessionConnect(Session session, LoginParameters parameters)
			throws ApplicationException;

	/**
	 * Gets called when a user closed the server connection or the server lost
	 * the connection
	 * 
	 * @param connection
	 *            The connection of the user
	 * @throws ApplicationException
	 */
	void onSessionDisconnect(Session session) throws ApplicationException;

	/**
	 * Gets called when the server gets started
	 * 
	 * @throws ApplicationException
	 */
	void init() throws ApplicationException;
}
