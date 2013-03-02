package ljas.commons.session;

import java.net.Socket;

import ljas.client.Client;
import ljas.commons.session.socket.DisconnectClientErrorHandler;
import ljas.commons.session.socket.DisconnectSessionErrorHandler;
import ljas.commons.session.socket.SocketSession;
import ljas.commons.session.socket.SocketSessionInputListener;
import ljas.commons.threading.ThreadSystem;

public final class SessionFactory {
	public static Session prepareSession(Client client,
			ThreadSystem threadSystem) {
		SocketSessionInputListener listener = new SocketSessionInputListener(
				threadSystem, new DisconnectClientErrorHandler(client));
		SocketSession socketSession = new SocketSession(listener);
		listener.setSession(socketSession);
		return socketSession;
	}

	public static Session createSocketSession(ThreadSystem threadSystem,
			Socket socket, SessionObserver observer) {
		SocketSessionInputListener listener = new SocketSessionInputListener(
				threadSystem);
		SocketSession socketSession = new SocketSession(listener, socket);
		listener.setSession(socketSession);
		listener.setErrorHandler(new DisconnectSessionErrorHandler(
				socketSession));
		listener.start();
		socketSession.setObserver(observer);
		return socketSession;
	}

}
