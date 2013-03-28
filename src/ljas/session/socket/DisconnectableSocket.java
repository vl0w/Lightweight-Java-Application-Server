package ljas.session.socket;

import java.io.IOException;
import java.net.Socket;

import ljas.exception.DisconnectException;
import ljas.session.Disconnectable;

public class DisconnectableSocket implements Disconnectable {

	private Socket socket;

	public DisconnectableSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void disconnect() throws DisconnectException {
		try {
			socket.close();
		} catch (IOException e) {
			throw new DisconnectException(e);
		}
	}

}
