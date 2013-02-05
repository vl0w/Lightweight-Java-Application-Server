package ljas.commons.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import ljas.commons.network.SocketConnection;
import ljas.commons.network.TaskSender;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.taskqueue.TaskController;
import ljas.commons.tools.MapUtil;
import ljas.commons.tools.QueueUtils;

import org.apache.log4j.Logger;

public class WorkerController {
	private TaskSender _local;
	private boolean _started;
	private List<Worker> _workers;

	public WorkerController(TaskSender local) {
		_workers = new CopyOnWriteArrayList<Worker>();
		_started = false;
		_local = local;

		addSocketWorkers(getTaskController().getConfiguration()
				.getSocketWorkers());
	}

	public TaskController getTaskController() {
		return getLocal().getTaskController();
	}

	public void setSocketWorker(SocketConnection clientConnection,
			SocketWorker value) {
		for (SocketWorker worker : getWorkers(SocketWorker.class)) {
			if (worker.getConnection() == clientConnection) {
				worker = value;
			}
		}
	}

	public SocketWorker getSocketWorker() throws Exception {
		for (SocketWorker worker : getWorkers(SocketWorker.class)) {
			if (worker.getConnection() == null) {
				return worker;
			}
		}
		throw new Exception();
	}

	public SocketWorker getSocketWorker(SocketConnection clientConnection) {
		for (SocketWorker w : getWorkers(SocketWorker.class)) {
			if (w.getConnection().equals(clientConnection)) {
				return w;
			}
		}
		return null;
	}

	public Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}

	public List<Worker> getWorkers() {
		return _workers;
	}

	public List<Worker> getWorkers(Class<? extends Worker>... types) {
		List<Worker> typeWorkerList = new ArrayList<Worker>();
		List<Worker> workerList = getWorkers();

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
	public <V extends Worker> List<V> getWorkers(Class<V> type) {
		List<V> typeWorkerList = new ArrayList<V>();
		List<Worker> workerList = getWorkers();

		for (Worker worker : workerList) {
			if (worker.getClass().equals(type)) {
				typeWorkerList.add((V) worker);
			}
		}
		return typeWorkerList;
	}

	private TaskSender getLocal() {
		return _local;
	}

	private TaskWorker getLazyTaskWorker(Task task) {
		List<TaskWorker> taskWorkerList = getWorkers(TaskWorker.class);
		int taskWorkerCount = taskWorkerList.size();
		int maxTaskWorkerCount = getTaskController().getConfiguration()
				.getMaxTaskWorkers();

		// First check: Create initial taskworker if there has none been before.
		if (taskWorkerCount == 0) {
			TaskWorker initialTaskWorker = addTaskWorker();
			return initialTaskWorker;
		}

		// Step 1: Make a ranking (workload) of all taskworkers
		Map<TaskWorker, Long> rankingList = new HashMap<TaskWorker, Long>();
		for (TaskWorker taskWorker : taskWorkerList) {
			List<Task> taskList = QueueUtils.toList(taskWorker.getTaskQueue());
			long estimatedExecutionTime = getTaskController()
					.getEstimatedExecutionTime(taskList);
			rankingList.put(taskWorker, new Long(estimatedExecutionTime));
		}

		// Step 2: Sort ranking list
		rankingList = MapUtil.sortByValue(rankingList);

		// Step 3: Average execution time of task
		long averageTimeForTask = getTaskController().getAverageExecutionTime(
				task);

		// Step 4: Decide
		TaskWorker laziestTaskWorker = rankingList.keySet().iterator().next();
		long laziestWorkersExecutionTime = rankingList.get(laziestTaskWorker);

		if (averageTimeForTask > laziestWorkersExecutionTime
				|| taskWorkerCount >= maxTaskWorkerCount) {
			return laziestTaskWorker;
		} else {
			return addTaskWorker();
		}
	}

	public boolean isStarted() {
		return _started;
	}

	@SuppressWarnings("unchecked")
	public void start() {
		// Start workers
		for (Worker w : getWorkers(TaskWorker.class, BackgroundWorker.class)) {
			w.start();
		}
		_started = true;
	}

	public void pauseAll() {
		for (Worker worker : getWorkers()) {
			worker.pause();
		}
	}

	public void killAll() {
		for (Worker worker : _workers) {
			worker.kill();
		}
		_workers = new ArrayList<Worker>();
		_started = false;
	}

	public void goAll() {
		for (Worker w : getWorkers()) {
			w.go();
		}
	}

	public void scheduleTask(Task task) {
		TaskWorker worker;
		do {
			worker = getLazyTaskWorker(task);
		} while (!worker.scheduleTask(task));
	}

	public TaskWorker addTaskWorker() {
		getLogger().debug("Adding new TaskWorker to handle the workload.");

		int existingTaskWorkers = getWorkers(TaskWorker.class).size();
		TaskWorker worker = new TaskWorker(this, ++existingTaskWorkers);
		if (isStarted()) {
			worker.start();
		}
		_workers.add(worker);
		getLogger().debug("New TaskWorker added");

		return worker;
	}

	public void addSocketWorkers(int n) {
		for (int i = 0; i < n; i++) {
			_workers.add(new SocketWorker(this));
		}
	}

	public void addBackgroundWorker(Task task) {
		_workers.add(new BackgroundWorker(this, task));
	}

	public void clearSuspendedSocketWorker(SocketWorker worker) {
		getWorkers(SocketWorker.class).remove(worker);
		getWorkers().add(new SocketWorker(this));
	}

}
