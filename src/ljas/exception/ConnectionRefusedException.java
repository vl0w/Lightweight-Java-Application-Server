package ljas.exception;

import ljas.state.login.LoginRefusedMessage;

public class ConnectionRefusedException extends Exception {
	private static final long serialVersionUID = 532763272097528071L;

	private LoginRefusedMessage refusedMessage;

	public ConnectionRefusedException(LoginRefusedMessage refusedMessage) {
		super(refusedMessage.getReason());
		this.refusedMessage = refusedMessage;
	}

	public LoginRefusedMessage getRefusedMessage() {
		return refusedMessage;
	}
}
