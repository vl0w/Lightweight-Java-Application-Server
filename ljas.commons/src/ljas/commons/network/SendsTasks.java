package ljas.commons.network;

import ljas.commons.application.Application;
import ljas.commons.exceptions.TaskReceiverNotFoundException;
import ljas.commons.tasking.taskspool.HasTaskSpool;

public interface SendsTasks extends HasTaskSpool{
	/**
	 * Gets the specific SocketConnection to which the sender sends tasks to.
	 * Examples:
	 * -The Client class returns the socket connection to the server.
	 * -The Server returns the socket connection to the client.
	 * @param connectionInfo Due to the server has more than one task receiver he needs some additional information to specify the correct client.
	 * @return null if the receiver could not be found
	 */
	public SocketConnection getTaskReceiver(ConnectionInfo connectionInfo) throws TaskReceiverNotFoundException, IllegalArgumentException;
	
	/**
	 * Gets the local connection information
	 */
	public ConnectionInfo getLocalConnectionInfo();
	
	
	public Application getApplication();
	
	/**
	 * Occurs when a task receiver has lost the connection
	 * 
	 * @param connectionInfo
	 */
	public void notifyDisconnectedTaskReceiver(ConnectionInfo connectionInfo);
}
