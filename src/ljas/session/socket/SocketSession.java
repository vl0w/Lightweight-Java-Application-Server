package ljas.session.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ljas.client.Client;
import ljas.exception.SessionException;
import ljas.session.Address;
import ljas.session.Session;
import ljas.session.SessionObserver;

import org.apache.log4j.Logger;

public class SocketSession implements Session {

	private Socket socket;
	private SessionObserver observer;
	private SocketInputListener inputListenerRunnable;
	private SocketFactory socketFactory;
	private Thread inputListenerThread;

	public SocketSession() {
		this.socket = null;
		this.observer = null;
		this.socketFactory = new SocketFactory();
		this.inputListenerRunnable = new SocketInputListener();
	}

	public SocketSession(Client client) {
		this();
		this.inputListenerRunnable = new SocketInputListener(client);
	}

	public SocketSession(Socket socket) {
		this();
		this.socket = socket;
		DisconnectableSocket disconnectableSocket = new DisconnectableSocket(
				socket);
		this.inputListenerRunnable = new SocketInputListener(
				disconnectableSocket);

		getInputListenerThread().start();
	}

	@Override
	public void connect(Address address) throws SessionException {
		if (socket != null) {
			disconnect();
		}

		try {
			socket = socketFactory.createSocket(address);
			socket.setKeepAlive(true);
		} catch (Exception e) {
			throw new SessionException(e);
		}

		getInputListenerThread().start();
	}

	@Override
	public void disconnect() {
		if (socket != null) {

			try {
				socket.close();
			} catch (IOException e) {
				getLogger().error("Error while disconnecting session " + this,
						e);
			}

			getInputListenerThread().interrupt();
			inputListenerThread = null;

			if (observer != null) {
				observer.onSessionDisconnected(this);
			}

			socket = null;
		}
	}

	@Override
	public synchronized void sendObject(Object obj) throws SessionException {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					socket.getOutputStream());
			outputStream.writeObject(obj);
		} catch (IOException e) {
			throw new SessionException(e);
		}
	}

	@Override
	public void setObserver(SessionObserver observer) {
		this.observer = observer;
	}

	@Override
	public boolean isConnected() {
		return socket != null && !socket.isClosed();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SocketSession [");

		if (socket == null) {
			sb.append("offline");
		} else {
			sb.append(socket.getInetAddress());
			sb.append(":");
			sb.append(socket.getPort());
		}

		sb.append("]");

		return sb.toString();
	}

	Socket getSocket() {
		return socket;
	}

	SessionObserver getObserver() {
		return observer;
	}

	void setSocketFactory(SocketFactory socketFactory) {
		this.socketFactory = socketFactory;
	}

	Thread getInputListenerThread() {
		if (inputListenerThread == null) {
			inputListenerRunnable.setSession(this);
			inputListenerThread = new Thread(inputListenerRunnable);
		}
		return inputListenerThread;
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}
}
