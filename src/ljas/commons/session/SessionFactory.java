package ljas.commons.session;

import java.net.Socket;

import ljas.client.Client;
import ljas.commons.session.socket.SocketSession;

public final class SessionFactory {
	public static Session prepareSession(Client client) {
		return new SocketSession(client);
	}

	public static Session createSocketSession(Socket socket) {
		return new SocketSession(socket);
	}

}
