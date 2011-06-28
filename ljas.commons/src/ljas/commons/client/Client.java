package ljas.commons.client;

import java.net.ConnectException;

import ljas.commons.application.LoginParameters;
import ljas.commons.application.client.ClientApplicationException;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.network.SendsTasks;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.task.TaskObserver;


public interface Client extends SendsTasks {
	/**
	 * Connects to a server
	 * 
	 * @param ip
	 *            The ip address of the server
	 * @param port
	 *            The port of the server
	 * @param parameters
	 *            The parameters. See {@link LoginParameters}
	 * @throws ConnectionRefusedException
	 *             Occurs when the server refused the connection attempt
	 * @throws ConnectException
	 *             Occurs when the server could not be reached
	 */
	public void connect(String ip, int port, LoginParameters parameters)
			throws ConnectionRefusedException, ConnectException;

	public void disconnect();

	public boolean isOnline();

	/**
	 * Runs a task synchronized on the client. This means that the client has to wait until the task is finished. 
	 * @param task The task to execute
	 * @return The executed task
	 * @throws ClientApplicationException When something went wrong
	 */
	public Task runTaskSync(Task task) throws ClientApplicationException;

	/**
	 * Runs a task asynchroniously on the server. Does not throw an exception in
	 * any case. Use {@link TaskObserver} to observer the tasks state
	 * 
	 * @param task
	 *            The task to send
	 * @return True when the task could be sended, false otherwise
	 */
	public boolean runTaskAsync(Task task);

	public ClientUI getUI();
}
