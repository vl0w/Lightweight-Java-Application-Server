package ljas.commons.application;

import java.io.Serializable;

import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.network.TaskSender;

public interface LoginParameters extends Serializable {
	/**
	 * Provided by the application class
	 * 
	 * @see Application
	 * @return The application version of the clients application
	 */
	public String getApplicationVersion();

	/**
	 * Provided by the application class
	 * 
	 * @see Application
	 * @return The application ID of the clients application
	 */
	public long getApplicationId();

	/**
	 * Is called, when a client tries to connect to the server. When an
	 * exception gets thrown, the message will be sended to the client!
	 * 
	 * @param server
	 * @throws Exception
	 *             The message of the exception will be sended back to the
	 *             client as an error-message!
	 */
	public void check(TaskSender server) throws ConnectionRefusedException;
}
