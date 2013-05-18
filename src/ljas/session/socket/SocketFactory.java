package ljas.session.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ljas.session.Address;

class SocketFactory {

	public Socket createSocket(Address address) throws IOException,
			UnknownHostException {
		return new Socket(address.getHost(), address.getPort());
	}
}
