package ljas.server.state;

import java.io.IOException;

import ljas.application.LoginParameters;
import ljas.exception.ApplicationException;
import ljas.exception.ConnectionRefusedException;

public interface ServerState {
	void checkClient(LoginParameters parameters)
			throws ConnectionRefusedException;

	void shutdown();

	void startup() throws ApplicationException, IOException;
}
