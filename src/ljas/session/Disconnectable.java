package ljas.session;

import ljas.exception.DisconnectException;

public interface Disconnectable {
	public void disconnect() throws DisconnectException;
}
