package ljas.commons.tasking.sendable.notification;

import java.io.Serializable;

import ljas.commons.network.SendsTasks;



public abstract class Notification implements Serializable{
	private static final long serialVersionUID = 6000905805251098476L;

	// MEMBERS
	private SendsTasks _local;
	
	// GETTERS & SETTERS
	/**
	 * Returns the taskspool where the notification notifies.
	 */
	public SendsTasks getLocal(){
		return _local;
	}
	public void setLocal(SendsTasks value){
		_local=value;
	}
	
	// CONSTRUCTOR
	
	// METHODS
	public abstract void notificate() throws Exception;
}
