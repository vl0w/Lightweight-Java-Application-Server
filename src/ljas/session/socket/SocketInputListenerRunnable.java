package ljas.session.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import ljas.exception.DisconnectException;
import ljas.session.Disconnectable;
import ljas.threading.RepeatingRunnable;

import org.apache.log4j.Logger;

class SocketInputListenerRunnable extends RepeatingRunnable {

	private SocketSession session;
	private Disconnectable disconnectable;

	public SocketInputListenerRunnable() {
	}

	public SocketInputListenerRunnable(Disconnectable disconnectable) {
		this.disconnectable = disconnectable;
	}

	public void setSession(SocketSession session) {
		this.session = session;
	}

	public void setDisconnectable(Disconnectable disconnectable) {
		this.disconnectable = disconnectable;
	}

	@Override
	protected void runCycle() {
		if (session != null) {
			try {
				Object receivedObject = readObjectFromSocketStream();
				notifyObservers(receivedObject);
			} catch (IOException e) {
				disconnect();
			} catch (ClassNotFoundException e) {
				getLogger().error("Session " + this + " sended unknown object",
						e);
			}
		}
	}

	private void disconnect() {
		if (disconnectable != null) {
			try {
				disconnectable.disconnect();
			} catch (DisconnectException e) {
				getLogger().error("Unable to disconnect " + disconnectable, e);
			}
		} else {
			getLogger()
					.warn("Unable to disconnect session/client due to occured IOException");
		}
	}

	private Object readObjectFromSocketStream() throws IOException,
			ClassNotFoundException {
		Socket socket = session.getSocket();
		ObjectInputStream inputStream = new ObjectInputStream(
				socket.getInputStream());
		return inputStream.readObject();
	}

	private void notifyObservers(Object receivedObject) {
		if (receivedObject != null) {
			session.getObserver().notiyObjectReceived(session, receivedObject);
		}
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}
}
