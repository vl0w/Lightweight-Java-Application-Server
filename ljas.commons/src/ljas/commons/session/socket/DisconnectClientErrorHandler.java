package ljas.commons.session.socket;

import java.io.IOException;
import java.net.Socket;

import ljas.commons.client.Client;
import ljas.commons.exceptions.SessionException;

import org.apache.log4j.Logger;

/**
 * Whenever a {@link IOException} from the {@link Socket} on which the
 * {@link SocketSessionInputListener} listens occurs, the {@link Client} gets
 * disconnected.
 * 
 * @author jonashansen
 * 
 */
public class DisconnectClientErrorHandler implements Runnable {

	private Client client;

	public DisconnectClientErrorHandler(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		try {
			client.disconnect();
		} catch (SessionException e) {
			Logger.getLogger(getClass())
					.error("Error while disconnecting client because of corrupt socket",
							e);
		}
	}
}
