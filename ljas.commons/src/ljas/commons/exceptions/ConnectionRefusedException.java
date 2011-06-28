package ljas.commons.exceptions;

import ljas.commons.state.RefusedMessage;

public class ConnectionRefusedException extends Exception {
	private static final long serialVersionUID = 532763272097528071L;

	private RefusedMessage _refusedMessage;

	public RefusedMessage getRefusedMessage() {
		return _refusedMessage;
	}

	public ConnectionRefusedException(RefusedMessage message) {
		super(message.getReason());
		_refusedMessage = message;
	}
}
