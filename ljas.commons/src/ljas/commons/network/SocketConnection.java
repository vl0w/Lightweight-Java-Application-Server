package ljas.commons.network;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.taskqueue.HasTaskQueue;

public class SocketConnection {
	protected HasTaskQueue _socketRoot;
	protected Socket _socket;

	public Socket getSocket() {
		return _socket;
	}

	private void setSocket(Socket value) {
		_socket = value;
	}

	private ObjectOutputStream getOut() throws IOException {
		return new ObjectOutputStream(getSocket().getOutputStream());
	}


	private ObjectInputStream getIn() throws IOException {
		return new ObjectInputStream(getSocket().getInputStream());
	}


	private void setSocketRoot(HasTaskQueue root) {
		_socketRoot = root;
	}

	private HasTaskQueue getSocketRoot() {
		return _socketRoot;
	}

	public ConnectionInfo getConnectionInfo() {
		return new ConnectionInfo(getSocket().getInetAddress().toString(),
				getSocket().getPort());
	}

	public SocketConnection(Socket socket, HasTaskQueue socketRoot) {
		try {
			setSocketRoot(socketRoot);
			setSocket(socket);
			getSocket().setKeepAlive(true);
		} catch (IOException e) {
			getSocketRoot().getLogger().error(e);
		}
	}

	@Override
	public String toString() {
		return getSocket().getInetAddress().toString();
	}

	public void close() {
		try {
			getSocket().close();
		} catch (Exception e) {
			getSocketRoot().getLogger().error(e, e);
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
