package ljas.commons.worker;

import ljas.commons.network.SocketConnection;
import ljas.commons.tasking.sendable.notification.Notification;
import ljas.commons.tasking.sendable.task.Task;
import ljas.commons.tasking.taskspool.WorkerController;

public class SocketWorker extends Worker {

	private SocketConnection _connection;

	public SocketConnection getConnection() {
		return _connection;
	}

	public void setConnection(SocketConnection value) {
		_connection = value;
		if (_connection != null) {
			if (!isAlive()) {
				start();
			}
		}
	}

	public SocketWorker(WorkerController controller) {
		super(controller, Thread.NORM_PRIORITY, false);
		setConnection(null);
		setName("SocketWorker");
	}

	@Override
	public void runItOnce() {
		try {
			Object o = _connection.readObject();
			if (o != null) {
				if (o instanceof Task) {
					if (getController().getTaskspool().checkTask((Task) o)) {
						getController().getTaskQueue().add((Task) o);
					}
				} else if (o instanceof Notification) {
					getController().getNotificationQueue()
							.add((Notification) o);
				} else {
					throw new Exception("Client sended unknown object!");
				}
			}
		} catch (Exception e) {
			_connection.close();
			kill();
		}
	}

	@Override
	public void kill() {
		getController().clearSuspendedSocketWorker(this);
		super.kill();
	}
}
