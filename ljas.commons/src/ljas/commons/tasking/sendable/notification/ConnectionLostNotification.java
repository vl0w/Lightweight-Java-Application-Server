package ljas.commons.tasking.sendable.notification;

import ljas.commons.network.ConnectionInfo;

public class ConnectionLostNotification extends Notification {
	private static final long serialVersionUID = -6986997194831381004L;
	
	private ConnectionInfo _connectionInfo;
	private boolean _notifyRemote;
	
	public ConnectionLostNotification(ConnectionInfo con, boolean notifyRemote){
		_connectionInfo=con;
		_notifyRemote=notifyRemote;
	}
	
	@Override
	public void notificate() {
		if(_notifyRemote){
			try {
				getLocal().getTaskSpool().getController().getSocketWorker(getLocal().getTaskReceiver(_connectionInfo)).pause();
				getLocal().getTaskReceiver(_connectionInfo).writeObject(new ConnectionLostNotification(getLocal().getLocalConnectionInfo(), false));
			} catch (Exception e){
				getLocal().getLogger().error(e);
			}
		}
		getLocal().notifyDisconnectedTaskReceiver(_connectionInfo);
	}

}
