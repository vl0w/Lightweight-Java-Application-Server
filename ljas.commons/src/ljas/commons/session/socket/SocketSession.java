package ljas.commons.session.socket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.net.SocketFactory;

import ljas.commons.exceptions.SessionException;
import ljas.commons.session.Session;
import ljas.commons.session.SessionObserver;

import org.apache.log4j.Logger;

public class SocketSession implements Session {

	private Socket socket;
	private SessionObserver observer;
	private SocketSessionInputListener listener;
	private SocketFactory socketFactory;

	public SocketSession(SocketSessionInputListener listener) {
		this.listener = listener;
		this.socketFactory = new SimpleSocketFactory();
	}

	public SocketSession(SocketSessionInputListener listener, Socket socket) {
		this.socket = socket;
		this.socketFactory = new SimpleSocketFactory();
		this.listener = listener;
	}

	@Override
	public void connect(String ip, int port) throws SessionException {
		if (socket != null) {
			disconnect();
		}

		try {
			socket = socketFactory.createSocket(ip, port);
		} catch (Exception e) {
			throw new SessionException(e);
		}

		if (!listener.isRunning()) {
			listener.start();
		}
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
			listener.forceKill();

			if (observer != null) {
				observer.notifySessionDisconnected(this);
			}

			socket = null;
		}
	}

	@Override
	public void sendObject(Object obj) throws SessionException {
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

	void setSocketFactory(SocketFactory socketFactory) {
		this.socketFactory = socketFactory;
	}

	void setSocket(Socket socket) {
		this.socket = socket;
	}

	Socket getSocket() {
		return socket;
	}

	SessionObserver getObserver() {
		return observer;
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}

}
