package ljas.commons.session;

import ljas.commons.exceptions.DisconnectException;

public interface Disconnectable {
	public void disconnect() throws DisconnectException;
}
