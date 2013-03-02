package ljas.commons.session.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import ljas.commons.exceptions.DisconnectException;
import ljas.commons.session.Disconnectable;
import ljas.commons.threading.RepetitiveThread;
import ljas.commons.threading.ThreadSystem;

public class SocketSessionInputListener extends RepetitiveThread {

	private SocketSession session;
	private Disconnectable disconnectable;

	public SocketSessionInputListener(ThreadSystem threadSystem,
			Disconnectable disconnectable) {
		super(threadSystem);
		this.disconnectable = disconnectable;
	}

	public SocketSessionInputListener(ThreadSystem threadSystem) {
		this(threadSystem, null);
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
			} catch (DisconnectException e1) {
				getLogger().error("Unable to disconnect " + disconnectable, e1);
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
}
