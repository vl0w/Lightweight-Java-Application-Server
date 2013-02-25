package ljas.commons.session.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import ljas.commons.threading.RepetitiveThread;
import ljas.commons.threading.ThreadSystem;

public class SocketSessionInputListener extends RepetitiveThread {

	private SocketSession session;

	public SocketSessionInputListener(ThreadSystem threadSystem) {
		super(threadSystem);
	}

	public void setSession(SocketSession session) {
		this.session = session;
	}

	@Override
	protected void runCycle() {
		if (session != null) {
			try {
				Object receivedObject = readObjectFromSocketStream();
				notifyObservers(receivedObject);
			} catch (IOException e) {
				session.disconnect();
			} catch (ClassNotFoundException e) {
				getLogger().error("Session " + this + " sended unknown object",
						e);
			}
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
