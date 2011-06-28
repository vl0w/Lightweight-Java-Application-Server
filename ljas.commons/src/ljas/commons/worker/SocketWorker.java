package ljas.commons.worker;

import ljas.commons.network.SocketConnection;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.taskqueue.WorkerController;

public class SocketWorker extends Worker {

	private SocketConnection _connection;
	private static int SOCKET_WORKER_COUNT = 0;

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
		setName("SocketWorker" + (++SOCKET_WORKER_COUNT));
	}

	@Override
	public void runItOnce() {
		try {
			Object o = _connection.readObject();
			if (o != null) {
				if (o instanceof Task) {
					if (getController().getTaskQueue().checkTask((Task) o)) {
						getController().getTaskQueue().addTask((Task) o);
					}
				} else {
					throw new Exception("Client sended unknown object!");
				}
			}
		} catch (Exception e) {
			// Close connection
			_connection.close();

			// Notify thread owner
			getController()
					.getTaskQueue()
					.getLocal()
					.notifyDisconnectedTaskReceiver(
							_connection.getConnectionInfo());

			// Kill this process
			kill();
		}
	}

	@Override
	public void kill() {
		getController().clearSuspendedSocketWorker(this);
		super.kill();
	}
}
