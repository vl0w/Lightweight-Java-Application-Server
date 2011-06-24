package ljas.commons.application.server;

import ljas.commons.application.Application;
import ljas.commons.application.LoginParameters;
import ljas.commons.network.SendsTasks;
import ljas.commons.network.SocketConnection;

public abstract class ServerApplication extends Application{
	// MEMBERS
	private SendsTasks _local;
	
	// GETTERS & SETTERS
	public SendsTasks getLocal(){
		return _local;
	}
	
	public void setLocal(SendsTasks value){
		_local=value;
	}
	
	// CONSTRUCTOR
	public ServerApplication(String name, String version){
		super(name,version);
	}
	
	// METHODS

	/**
	 * Gets called, when a user connects to the server
	 * @param connection The connection to the user
	 * @param parameter The login parameters
	 * @throws ServerApplicationException
	 */
	public abstract void registerUser(SocketConnection connection, LoginParameters parameter) throws ServerApplicationException;
	
	/**
	 * Gets called, when a user closed the server connection or the server lost the connection
	 * @param connection The connection of the user
	 * @throws ServerApplicationException
	 */
	public abstract void removeUser(SocketConnection connection) throws ServerApplicationException;
}
