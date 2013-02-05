package ljas.commons.worker;

import ljas.commons.network.SocketConnection;
import ljas.commons.tasking.Task;

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
				int port = getWorkerController().getTaskController().getLocal()
						.getLocalConnectionInfo().getPort();
				setName("SocketWorker " + SOCKET_WORKER_COUNT + " (:" + port
						+ ")");
				start();
			}
		}
	}

	public SocketWorker(WorkerController controller) {
		super(controller, Thread.NORM_PRIORITY, false);
		setConnection(null);
		setName("SocketWorker (suspended)");
		SOCKET_WORKER_COUNT++;
	}

	@Override
	public void runCycle() {
		try {
			Object o = _connection.readObject();
			if (o != null) {
				if (o instanceof Task) {
					if (getWorkerController().getTaskController().checkTask(
							(Task) o)) {
						getWorkerController().getTaskController().scheduleTask(
								(Task) o);
					}
				} else {
					throw new Exception("Client sended unknown object!");
				}
			}
		} catch (Exception e) {
			// Close connection
			_connection.close();

			// Notify thread owner
			getWorkerController()
					.getTaskController()
					.getLocal()
					.notifyDisconnectedTaskReceiver(
							_connection.getConnectionInfo());

			// Kill this process
			kill();
		}
	}

	@Override
	public void kill() {
		getWorkerController().clearSuspendedSocketWorker(this);
		super.kill();
	}
}
