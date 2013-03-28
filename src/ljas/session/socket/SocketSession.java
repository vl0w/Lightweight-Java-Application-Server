package ljas.session.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.net.SocketFactory;

import ljas.client.Client;
import ljas.exception.SessionException;
import ljas.session.Session;
import ljas.session.SessionObserver;

import org.apache.log4j.Logger;

public class SocketSession implements Session {

	private Socket socket;
	private SessionObserver observer;
	private SocketInputListenerRunnable inputListenerRunnable;
	private SocketFactory socketFactory;
	private Thread inputListenerThread;

	public SocketSession() {
		this.socket = null;
		this.observer = null;
		this.socketFactory = new SimpleSocketFactory();
		this.inputListenerRunnable = new SocketInputListenerRunnable();
	}

	public SocketSession(Client client) {
		this();
		this.inputListenerRunnable = new SocketInputListenerRunnable(client);
	}

	public SocketSession(Socket socket) {
		this();
		this.socket = socket;
		DisconnectableSocket disconnectableSocket = new DisconnectableSocket(
				socket);
		this.inputListenerRunnable = new SocketInputListenerRunnable(
				disconnectableSocket);

		getInputListenerThread().start();
	}

	@Override
	public void connect(String ip, int port) throws SessionException {
		if (socket != null) {
			disconnect();
		}

		try {
			socket = socketFactory.createSocket(ip, port);
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
				observer.notifySessionDisconnected(this);
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
