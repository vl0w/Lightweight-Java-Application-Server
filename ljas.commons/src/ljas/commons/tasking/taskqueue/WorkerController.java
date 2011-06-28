package ljas.commons.tasking.taskqueue;

import java.util.ArrayList;

import ljas.commons.network.SocketConnection;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.task.TaskState;
import ljas.commons.worker.BackgroundWorker;
import ljas.commons.worker.SocketWorker;
import ljas.commons.worker.TaskWorker;
import ljas.commons.worker.Worker;

public class WorkerController {
	private final ArrayList<TaskWorker> _taskWorkers;
	private final ArrayList<BackgroundWorker> _backgroundWorker;
	private final ArrayList<SocketWorker> _socketWorkers;
	private final TaskQueue _taskQueue;

	public void addTaskWorkers(int n) {
		for (int i = 0; i < n; i++) {
			_taskWorkers.add(new TaskWorker(this, "TaskWorker " + i));
		}
	}

	public void addSocketWorkers(int n) {
		for (int i = 0; i < n; i++) {
			_socketWorkers.add(new SocketWorker(this));
		}
	}

	public void addBackgroundWorker(Task task) {
		_backgroundWorker.add(new BackgroundWorker(this, task));
	}

	public ArrayList<Worker> getWorkers() {
		ArrayList<Worker> workers = new ArrayList<Worker>();
		workers.addAll(_taskWorkers);
		workers.addAll(_backgroundWorker);
		workers.addAll(_socketWorkers);
		return workers;
	}

	public TaskQueue getTaskQueue() {
		return _taskQueue;
	}

	public WorkerController(TaskQueue taskQueue, int taskWorkers,
			int socketWorkers) {
		_taskQueue = taskQueue;
		_taskWorkers = new ArrayList<TaskWorker>();
		_backgroundWorker = new ArrayList<BackgroundWorker>();
		_socketWorkers = new ArrayList<SocketWorker>();

		addTaskWorkers(taskWorkers);
		addSocketWorkers(socketWorkers);
	}

	public void start() {
		// Start workers
		for (Worker w : getWorkers()) {
			if (!(w instanceof SocketWorker)) {
				w.start();
			}
		}
	}

	public SocketWorker getSocketWorker() throws Exception {
		for (SocketWorker w : _socketWorkers) {
			if (w.getConnection() == null) {
				return w;
			}
		}
		throw new Exception();
	}

	public SocketWorker getSocketWorker(SocketConnection clientConnection) {
		for (SocketWorker w : _socketWorkers) {
			if (w.getConnection().equals(clientConnection)) {
				return w;
			}
		}
		return null;
	}

	public void setSocketWorker(SocketConnection clientConnection,
			SocketWorker value) {
		for (SocketWorker w : _socketWorkers) {
			if (w.getConnection() == clientConnection) {
				w = value;
			}
		}
	}

	public void clearSuspendedSocketWorker(SocketWorker wrk) {
		_socketWorkers.remove(wrk);
		_socketWorkers.add(new SocketWorker(this));
	}

	public void waitAll() {
		for (Worker w : getWorkers()) {
			w.pause();
		}
	}

	public void goAll() {
		for (Worker w : getWorkers()) {
			w.go();
		}
	}
}
