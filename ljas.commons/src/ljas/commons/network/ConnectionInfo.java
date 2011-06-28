package ljas.commons.network;

import java.io.Serializable;

public class ConnectionInfo implements Serializable {
	private static final long serialVersionUID = -1556152260175845364L;

	private String _ip;
	private int _port;

	public String getIp() {
		return _ip;
	}

	public Integer getPort() {
		return _port;
	}

	public ConnectionInfo(String ip, int port) {
		_ip = ip;
		_port = port;
	}

	@Override
	public String toString() {
		return "ConnectionInfo [" + _ip + ":" + _port + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConnectionInfo) {
			ConnectionInfo cobj = (ConnectionInfo) obj;
			return getPort().equals(cobj.getPort())
					&& getIp().equals(cobj.getIp());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return _ip.hashCode() + _port;
	}
}
