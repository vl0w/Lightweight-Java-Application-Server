package ljas.commons.tasking.taskspool;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ljas.commons.network.SocketConnection;
import ljas.commons.tasking.sendable.task.Task;
import ljas.commons.tasking.sendable.task.TaskState;
import ljas.commons.worker.BackgroundWorker;
import ljas.commons.worker.SocketWorker;
import ljas.commons.worker.TaskWorker;
import ljas.commons.worker.Worker;

public class WorkerController {
	// MEMBERS
	private Queue<Task> _taskQueue;
	private final ArrayList<TaskWorker> _taskWorkers;
	private final ArrayList<BackgroundWorker> _backgroundWorker;
	private final ArrayList<SocketWorker> _socketWorkers;
	private final TaskSpool _taskSpool;

	// GETTERS & SETTERS
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

	public TaskSpool getTaskspool() {
		return _taskSpool;
	}

	private Queue<Task> getTaskQueue() {
		return _taskQueue;
	}

	public int getTaskQueueSize() {
		return getTaskQueue().size();
	}

	// CONSTRUCTORS
	public WorkerController(TaskSpool taskSpool, int taskWorkers,
			int socketWorkers) {
		_taskSpool = taskSpool;
		_taskWorkers = new ArrayList<TaskWorker>();
		_backgroundWorker = new ArrayList<BackgroundWorker>();
		_socketWorkers = new ArrayList<SocketWorker>();
		_taskQueue = new ConcurrentLinkedQueue<Task>();

		addTaskWorkers(taskWorkers);
		addSocketWorkers(socketWorkers);
	}

	// METHODS
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

	/**
	 * Adds the task to the queue. The task has to have the state DO_PERFORM or
	 * DO_CHECK!
	 * 
	 * @param task
	 *            The task to add to the queue
	 */
	public void addTask(Task task) {
		if (task.getState() != TaskState.FINISHED
				&& task.getState() != TaskState.NEW) {
			getTaskspool().getLogger().debug(
					"Added task '" + task + "' to queue");

			getTaskQueue().add(task);
		}
	}

	public Task removeTask() {
		return getTaskQueue().remove();
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
