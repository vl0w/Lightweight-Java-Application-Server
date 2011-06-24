package ljas.commons.state;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RefusedMessage implements Serializable {
	public static RefusedMessage SERVER_FULL=new RefusedMessage("Server is full");
	public static RefusedMessage ILLEGAL_STATE=new RefusedMessage("Server has illegal state");
	public static RefusedMessage WRONG_APPLICATION_VERSION=new RefusedMessage("Client has not same application version as server");
	public static RefusedMessage WRONG_APPLICATION_ID=new RefusedMessage("Client has not same application as server");
	public static RefusedMessage UNKNOWN_EXCEPTION_OCCURED=new RefusedMessage("Unknown server exception occured");
	
	// MEMBERS
	private final String _reason;
	
	// GETTERS & SETTERS
	public String getReason(){
		return _reason;
	}
	
	// CONSTRUCTORS
	public RefusedMessage(String reason){
		_reason=reason;
	}
	
	// METHODS
	@Override
	public boolean equals(Object obj){
		if(obj instanceof RefusedMessage){
			RefusedMessage rmObj = (RefusedMessage) obj;
			if(rmObj.getReason().equals(getReason())){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return getReason().hashCode();
	}
}
