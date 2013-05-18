package ljas.client;

import ljas.application.Application;
import ljas.exception.ApplicationException;
import ljas.exception.ConnectionRefusedException;
import ljas.exception.SessionException;
import ljas.session.Disconnectable;
import ljas.session.Session;
import ljas.tasking.Task;
import ljas.tasking.observation.TaskObserver;

public interface Client extends Disconnectable {
	/**
	 * The default time after a server request timeout occurs
	 */
	static long REQUEST_TIMEOUT_MS = 20000;

	/**
	 * Connects to a server
	 * 
	 * @param ip
	 *            The ip address of the server
	 * @param port
	 *            The port of the server
	 * @throws ConnectionRefusedException
	 *             Occurs when the server refused the connection attempt
	 * @throws ConnectException
	 *             Occurs when the server could not be contacted
	 */
	void connect(String ip, int port) throws ConnectionRefusedException,
			SessionException;

	boolean isOnline();

	/**
	 * Runs a task synchronized on the client. This means that the client has to
	 * wait until the task is finished.
	 * 
	 * @param task
	 *            The task to execute
	 * @return The executed task
	 * @throws ApplicationException
	 * @throws ClientApplicationException
	 *             When something went wrong
	 */
	<T extends Task> T runTaskSync(T task) throws ApplicationException;

	/**
	 * Runs a task asynchroniously on the server. Does not throw an exception in
	 * any case. Use {@link TaskObserver} to observer the tasks state
	 * 
	 * @param task
	 *            The task to send
	 * @return True when the task could be sended, false otherwise
	 */
	void runTaskAsync(Task task);

	Session getServerSession();

	<A extends Application> A getApplication();
}
