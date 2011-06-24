package ljas.commons.tasking.taskspool;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ljas.commons.network.SocketConnection;
import ljas.commons.tasking.sendable.notification.Notification;
import ljas.commons.tasking.sendable.task.Task;
import ljas.commons.worker.BackgroundWorker;
import ljas.commons.worker.NotificationWorker;
import ljas.commons.worker.SocketWorker;
import ljas.commons.worker.TaskWorker;
import ljas.commons.worker.Worker;


public class WorkerController {
	// MEMBERS
	private final Queue<Task> _taskQueue;
	private final Queue<Notification> _notificationQueue;
	private final ArrayList<TaskWorker> _taskWorkers;
	private final ArrayList<NotificationWorker> _notificationWorkers;
	private final ArrayList<BackgroundWorker> _backgroundWorker;
	private final ArrayList<SocketWorker> _socketWorkers;
	private final TaskSpool _taskSpool;

	// GETTERS & SETTERS
	public void addTaskWorkers(int n) {
		for (int i = 0; i < n; i++) {
			_taskWorkers.add(new TaskWorker(this));
		}
	}

	public void addNotificationWorkers(int n) {
		for (int i = 0; i < n; i++) {
			_notificationWorkers.add(new NotificationWorker(this));
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
		workers.addAll(_notificationWorkers);
		workers.addAll(_backgroundWorker);
		workers.addAll(_socketWorkers);
		return workers;
	}

	public TaskSpool getTaskspool() {
		return _taskSpool;
	}
	
	public Queue<Task> getTaskQueue(){
		return _taskQueue;
	}
	
	public Queue<Notification> getNotificationQueue() {
		return _notificationQueue;
	}

	// CONSTRUCTORS
	public WorkerController(TaskSpool taskSpool, int taskWorkers,
			int socketWorkers, int notificationWorkers) {
		_taskSpool = taskSpool;
		_taskWorkers = new ArrayList<TaskWorker>();
		_notificationWorkers = new ArrayList<NotificationWorker>();
		_backgroundWorker = new ArrayList<BackgroundWorker>();
		_socketWorkers = new ArrayList<SocketWorker>();
		_taskQueue = new ConcurrentLinkedQueue<Task>();
		_notificationQueue = new ConcurrentLinkedQueue<Notification>();

		addTaskWorkers(taskWorkers);
		addSocketWorkers(socketWorkers);
		addNotificationWorkers(notificationWorkers);
	}

	// METHODS
	public void toCache(Task t) {
		_taskQueue.add(t);
	}

	public void start() {
		// Start workers
		for (Worker w : getWorkers()) {
			if (!(w instanceof SocketWorker)) {
				w.start();
			}
		}
	}

	public SocketWorker getSocketWorker()
			throws Exception {
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
