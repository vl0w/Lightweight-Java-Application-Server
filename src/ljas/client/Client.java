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
	long REQUEST_TIMEOUT_MS = 20000;

	void connect(String ip, int port) throws ConnectionRefusedException,
			SessionException;

	boolean isOnline();

	/**
	 * Runs a task synchronized on the client. <br>
	 * The client waits until the server has processed and returned the request
	 * back to the client.
	 * 
	 * @param task
	 *            The task to execute
	 * @return The executed task
	 * @throws ApplicationException
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
