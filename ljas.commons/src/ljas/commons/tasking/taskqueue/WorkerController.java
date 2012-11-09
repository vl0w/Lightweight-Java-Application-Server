package ljas.commons.tasking.taskqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ljas.commons.network.SocketConnection;
import ljas.commons.tasking.task.Task;
import ljas.commons.worker.BackgroundWorker;
import ljas.commons.worker.SocketWorker;
import ljas.commons.worker.TaskWorker;
import ljas.commons.worker.Worker;


public class WorkerController {
	private List<Worker>	_workerList;
	private final TaskQueue	_taskQueue;

	public void addTaskWorkers(int n) {
		for (int i = 0; i < n; i++) {
			_workerList.add(new TaskWorker(this, "TaskWorker " + i));
		}
	}

	public void addSocketWorkers(int n) {
		for (int i = 0; i < n; i++) {
			_workerList.add(new SocketWorker(this));
		}
	}

	public void addBackgroundWorker(Task task) {
		_workerList.add(new BackgroundWorker(this, task));
	}

	private List<Worker> getWorkerList() {
		return _workerList;
	}

	private List<Worker> getWorkerList(Class<? extends Worker>... types) {
		List<Worker> typeWorkerList = new ArrayList<Worker>();
		List<Worker> workerList = getWorkerList();

		for (Worker worker : workerList) {
			for (Class<? extends Worker> workerClass : types) {
				if (worker.getClass().equals(workerClass)) {
					typeWorkerList.add(worker);
				}
			}
		}
		return typeWorkerList;
	}

	@SuppressWarnings("unchecked")
	private <V extends Worker> List<V> getWorkerList(Class<V> type) {
		List<V> typeWorkerList = new ArrayList<V>();
		List<Worker> workerList = getWorkerList();

		for (Worker worker : workerList) {
			if (worker.getClass().equals(type)) {
				typeWorkerList.add((V) worker);
			}
		}
		return typeWorkerList;
	}

	public TaskQueue getTaskQueue() {
		return _taskQueue;
	}

	public WorkerController(TaskQueue taskQueue) {
		_taskQueue = taskQueue;
		_workerList = new CopyOnWriteArrayList<Worker>();

		addTaskWorkers(getTaskQueue().getConfiguration().getTaskWorkers());
		addSocketWorkers(getTaskQueue().getConfiguration().getSocketWorkers());
	}

	@SuppressWarnings("unchecked")
	public void start() {
		// Start workers
		for (Worker w : getWorkerList(TaskWorker.class, BackgroundWorker.class)) {
			w.start();
		}
	}

	public SocketWorker getSocketWorker() throws Exception {
		for (SocketWorker worker : getWorkerList(SocketWorker.class)) {
			if (worker.getConnection() == null) {
				return worker;
			}
		}
		throw new Exception();
	}

	public SocketWorker getSocketWorker(SocketConnection clientConnection) {
		for (SocketWorker w : getWorkerList(SocketWorker.class)) {
			if (w.getConnection().equals(clientConnection)) {
				return w;
			}
		}
		return null;
	}

	public void setSocketWorker(SocketConnection clientConnection, SocketWorker value) {
		for (SocketWorker worker : getWorkerList(SocketWorker.class)) {
			if (worker.getConnection() == clientConnection) {
				worker = value;
			}
		}
	}

	public void clearSuspendedSocketWorker(SocketWorker worker) {
		getWorkerList(SocketWorker.class).remove(worker);
		getWorkerList().add(new SocketWorker(this));
	}

	public void pauseAll() {
		for (Worker worker : getWorkerList()) {
			worker.pause();
		}
	}

	public void killAll() {
		for (Worker worker : _workerList) {
			worker.kill();
		}
	}

	public void goAll() {
		for (Worker w : getWorkerList()) {
			w.go();
		}
	}
}
