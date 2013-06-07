package ljas.server.state;

import java.io.IOException;

import ljas.application.LoginParameters;
import ljas.exception.ApplicationException;
import ljas.exception.ConnectionRefusedException;
import ljas.state.login.LoginRefusedMessage;

public class OfflineState implements ServerState {

	@Override
	public void checkClient(LoginParameters parameters)
			throws ConnectionRefusedException {
		throw new ConnectionRefusedException(LoginRefusedMessage.ILLEGAL_STATE);
	}

	@Override
	public void startup() throws ApplicationException, IOException {
		throw new IllegalStateException();
	}

	@Override
	public void shutdown() {
		throw new IllegalStateException();
	}

}
