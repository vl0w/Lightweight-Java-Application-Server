package ljas.commons.application.server;

import ljas.commons.exceptions.TaskException;

public class ServerApplicationException extends TaskException {
	private static final long serialVersionUID = 7169926723008873161L;

	public ServerApplicationException(String message) {
		super(message);
	}
}
