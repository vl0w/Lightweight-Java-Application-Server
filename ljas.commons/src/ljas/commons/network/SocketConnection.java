package ljas.commons.network;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ljas.commons.tasking.sendable.notification.ConnectionLostNotification;
import ljas.commons.tasking.sendable.task.Task;
import ljas.commons.tasking.taskspool.HasTaskSpool;


public class SocketConnection {
	// MEMBERS
	protected HasTaskSpool _socketRoot;
	protected Socket _socket;
	protected ObjectOutputStream _out;
	protected ObjectInputStream _in;

	// GETTERS & SETTERS
	public Socket getSocket() {
		return _socket;
	}

	private void setSocket(Socket value) {
		_socket = value;
	}

	private ObjectOutputStream getOut() {
		return _out;
	}

	private void setOut(ObjectOutputStream value) {
		_out = value;
	}

	private ObjectInputStream getIn() {
		return _in;
	}

	private void setIn(ObjectInputStream value) {
		_in = value;
	}

	private void setSocketRoot(HasTaskSpool root) {
		_socketRoot = root;
	}

	private HasTaskSpool getSocketRoot() {
		return _socketRoot;
	}

	public ConnectionInfo getConnectionInfo() {
		return new ConnectionInfo(getSocket().getInetAddress().toString(),
				getSocket().getPort());
	}

	// CONSTRUCTORS
	public SocketConnection(Socket socket, HasTaskSpool socketRoot) {
		try {
			setSocketRoot(socketRoot);
			setSocket(socket);
			getSocket().setKeepAlive(true);
			setOut(new ObjectOutputStream(getSocket().getOutputStream()));
			setIn(new ObjectInputStream(getSocket().getInputStream()));
		} catch (IOException e) {
			getSocketRoot().getLogger().error(e);
		}
	}

	// METHODS
	@Override
	public String toString() {
		return getSocket().getInetAddress().toString();
	}

	public synchronized void close() {
		if (!getSocket().isClosed()) {
			getSocketRoot()
					.getTaskSpool()
					.getController()
					.getNotificationQueue()
					.add(new ConnectionLostNotification(this
							.getConnectionInfo(), false));
			try {
				getOut().close();
				getIn().close();
				getSocket().close();
			} catch (Exception e) {
				getSocketRoot().getLogger().error(e);
			}
		}
	}

	public Task readTask() throws Exception {
		Object o = readObject();
		if (o != null && o instanceof Task) {
			return (Task) o;
		}
		return null;
	}

	public Object readObject() throws Exception {
		try {
			return getIn().readObject();
		} catch (Exception e) {
			close();
			throw new Exception(e);
		}
	}

	public synchronized void writeObject(Object outputObject) {
		try {
			getOut().writeObject(outputObject);
		} catch (NotSerializableException e) {
			getSocketRoot().getLogger().error(e);
		} catch (Exception e) {
			close();
		}
	}
}
