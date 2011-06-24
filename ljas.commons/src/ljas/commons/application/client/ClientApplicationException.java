package ljas.commons.application.client;

import ljas.commons.exceptions.TaskException;


public class ClientApplicationException extends TaskException {
	private static final long serialVersionUID = -5654461562758170368L;
	
	public ClientApplicationException(String message){
		super(message);
	}
	public ClientApplicationException(Throwable t){
		super(t);
	}
}
