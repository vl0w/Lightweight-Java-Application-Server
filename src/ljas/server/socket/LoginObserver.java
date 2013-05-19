package ljas.server.socket;

import java.net.Socket;

public interface LoginObserver {
	void onSocketConnect(Socket socket);
}
