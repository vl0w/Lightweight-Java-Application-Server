package ljas.commons.network;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ConnectionInfo implements Serializable {
	// MEMBERS
	private String _ip;
	private int _port;
	
	// GETTERS & SETTERS
	public String getIp(){
		return _ip;
	}
	
	public Integer getPort(){
		return _port;
	}
	
	// CONSTRUCTOR
	public ConnectionInfo(String ip, int port){
		_ip=ip;
		_port=port;
	}
	
	// METHODS
	@Override
	public String toString() {
		return "ConnectionInfo [_ip=" + _ip + ", _port=" + _port + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ConnectionInfo){
			ConnectionInfo cobj=(ConnectionInfo)obj;
			return getPort().equals(cobj.getPort()) && getIp().equals(cobj.getIp());
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return _ip.hashCode()+_port;
	}
}
