package ljas.commons.session.socket;

import java.io.IOException;
import java.net.Socket;

import ljas.commons.exceptions.SessionException;
import ljas.commons.session.Session;

import org.apache.log4j.Logger;

/**
 * Whenever a {@link IOException} from the {@link Socket} on which the
 * {@link SocketSessionInputListener} listens occurs, the {@link Session} gets
 * disconnected.
 * 
 * @author jonashansen
 * 
 */
public class DisconnectSessionErrorHandler implements Runnable {

	private Session session;

	public DisconnectSessionErrorHandler(Session session) {
		this.session = session;
	}

	@Override
	public void run() {
		try {
			session.disconnect();
		} catch (SessionException e) {
			Logger.getLogger(getClass())
					.error("Error while disconnecting session because of corrupt socket",
							e);
		}
	}

}
