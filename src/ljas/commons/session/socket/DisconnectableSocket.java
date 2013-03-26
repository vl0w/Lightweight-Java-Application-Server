package ljas.commons.session.socket;

import java.io.IOException;
import java.net.Socket;

import ljas.commons.exceptions.DisconnectException;
import ljas.commons.session.Disconnectable;

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
