package ljas.commons.network;

import ljas.commons.application.Application;
import ljas.commons.exceptions.TaskReceiverNotFoundException;
import ljas.commons.tasking.taskqueue.HasTaskQueue;

public interface TaskSender extends HasTaskQueue {
	/**
	 * Gets the specific SocketConnection to which the sender sends tasks to.
	 * <br>Examples: 
	 * <ul>
	 * <li>The Client class returns the socketconnection to the server</li>
	 * <li>The Server returns the socket connection to a client</li>
	 * </ul>
	 * 
	 * @param connectionInfo
	 *            Due to the server has more than one task receiver he needs
	 *            some additional information to specify the correct client. Nullable.
	 * @return null if the receiver could not be found
	 */
	public SocketConnection getTaskReceiver(ConnectionInfo connectionInfo)
			throws TaskReceiverNotFoundException, IllegalArgumentException;

	/**
	 * Returns the local connection information
	 */
	public ConnectionInfo getLocalConnectionInfo();

	/**
	 * Returns the application of the TaskSender
	 */
	public Application getApplication();

	/**
	 * Occurs when a task receiver has lost the connection
	 * 
	 * @param connectionInfo
	 */
	public void notifyDisconnectedTaskReceiver(ConnectionInfo connectionInfo);
}
