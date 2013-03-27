package ljas.client;

import java.io.IOException;
import java.net.ConnectException;

import ljas.commons.application.Application;
import ljas.commons.application.LoginParameters;
import ljas.commons.exceptions.ApplicationException;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.exceptions.DisconnectException;
import ljas.commons.exceptions.SessionException;
import ljas.commons.session.Disconnectable;
import ljas.commons.session.Session;
import ljas.commons.state.HasState;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.HasTaskSystem;
import ljas.commons.tasking.observation.TaskObserver;

public interface Client extends HasTaskSystem, HasState, Disconnectable {
	/**
	 * The default time after a server request timeout occurs
	 */
	static long REQUEST_TIMEOUT_MS = 10000;

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
	 * @throws DisconnectException
	 * @throws ConnectException
	 *             Occurs when the server could not be reached
	 * @throws IOException
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
	<V extends Task> V runTaskSync(V task) throws ApplicationException;

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
