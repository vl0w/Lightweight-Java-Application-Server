package ljas.commons.tasking.taskqueue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ljas.commons.network.ConnectionInfo;
import ljas.commons.network.TaskSender;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskResult;
import ljas.commons.tasking.TaskState;
import ljas.commons.tools.Average;
import ljas.commons.worker.WorkerController;

import org.apache.log4j.Logger;

public class TaskController {
	public final int WORKER_DELAY = 5;
	private static long TASKIDCOUNTER = 0;
	private TaskQueueConfiguration _configuration;
	private boolean _initialized;
	private Map<Class<? extends Task>, Average> _executionTimeMap;

	public TaskController(TaskQueueConfiguration configuration) {
		setConfiguration(configuration);
		setInitialized(false);
		_executionTimeMap = new ConcurrentHashMap<Class<? extends Task>, Average>();
	}

	public void storeExecutionTime(Task task, long millis) {
		Class<? extends Task> clazz = task.getClass();

		if (!_executionTimeMap.containsKey(clazz)) {
			_executionTimeMap.put(clazz, new Average());
		}
		Average average = _executionTimeMap.get(clazz);
		average.add(millis);
	}

	public long getAverageExecutionTime(Task task) {
		Class<? extends Task> clazz = task.getClass();
		if (_executionTimeMap.containsKey(clazz)) {
			Average average = _executionTimeMap.get(clazz);
			return average.asLong();
		} else {
			return 0;
		}
	}

	public long getEstimatedExecutionTime(List<Task> tasks) {
		long estimatedTime = 0;

		// Loop over every task
		for (Task task : tasks) {
			long averageExecutionTime = getAverageExecutionTime(task);
			estimatedTime += averageExecutionTime;
		}

		return estimatedTime;
	}

	public TaskSender getLocal() {
		return getConfiguration().getLocal();
	}

	public Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}

	private WorkerController getWorkerController() {
		return getLocal().getWorkerController();
	}

	public boolean isInitialized() {
		return _initialized;
	}

	private void setInitialized(boolean initialized) {
		_initialized = initialized;
	}

	public TaskQueueConfiguration getConfiguration() {
		return _configuration;
	}

	private void setConfiguration(TaskQueueConfiguration configuration) {
		_configuration = configuration;
	}

	/**
	 * Adds the task to the queue. The task has to have the state DO_PERFORM or
	 * DO_CHECK!
	 * 
	 * @param task
	 *            The task to add to the queue
	 */
	public void scheduleTask(Task task) {
		prepareTaskForExecution(task);

		if (task.getState() != TaskState.FINISHED
				&& task.getState() != TaskState.NEW) {
			getLogger().debug("Scheduled task '" + task + "'");
			getWorkerController().scheduleTask(task);
		}
	}

	public void addBackgroundTask(Task task) {
		task.setLocal(getLocal());
		getWorkerController().addBackgroundWorker(task);
	}

	public void activate(Task... backgroundTasks) {
		if (!isInitialized()) {
			initialize();
		}

		// Add background tasks
		for (Task task : backgroundTasks) {
			getWorkerController().addBackgroundWorker(task);
		}
	}

	/**
	 * Prepares a task for performing
	 * 
	 * @param task
	 *            The task to prepare
	 * @return The prepared task
	 */
	private void prepareTaskForExecution(Task task) {
		// Set local to null. Otherwise NotSerializableException
		task.setLocal(null);

		if (task.getState() == TaskState.NEW) {
			// Complete TaskHeader
			task.setApplicationId(getLocal().getApplication()
					.getApplicationId());
			task.setSenderInfo(getLocal().getLocalConnectionInfo());

			// Set state
			task.setState(TaskState.DO_PERFORM);
		}
	}

	public void executeTaskRemote(Task task, ConnectionInfo connectionInfo)
			throws Exception {
		prepareTaskForExecution(task);
		getLocal().getTaskReceiver(connectionInfo).writeObject(task);
	}

	public void executeTaskLocal(Task task) throws Exception {
		scheduleTask(task);
	}

	public static long createTaskId() {
		return ++TASKIDCOUNTER;
	}

	/**
	 * Deactivates the {@link TaskController}
	 */
	public void deactivate() {
		getWorkerController().killAll();
		setInitialized(false);
	}

	/**
	 * Initialize the {@link TaskController} to its initial state
	 */
	private void initialize() {
		setInitialized(true);
	}

	/**
	 * <ol>
	 * <li>Checks a task. If the task is not correct (not same applId or
	 * applVersion) it will automatically be sent back with an error.</li>
	 * </ol>
	 * 
	 * @param task
	 *            The task to be checked
	 * @return True when the task is all right
	 * @throws Exception
	 */
	public boolean checkTask(Task task) throws Exception {
		/*
		 * Wrong application id, do not execute due to safety concerns
		 */
		if (getLocal().getApplication().getApplicationId() != task
				.getApplicationId() && task.getState() == TaskState.DO_PERFORM) {
			getLogger().warn(Task.MSG_SAFETY_CONCERN);

			task.setResult(TaskResult.ERROR);
			task.setState(TaskState.DO_CHECK);
			task.setResultMessage(Task.MSG_SAFETY_CONCERN);
			executeTaskRemote(task, task.getSenderInfo());

			return false;
		}
		return true;
	}
}
