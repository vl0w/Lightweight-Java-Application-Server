package ljas.commons.tasking.taskqueue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import ljas.commons.network.ConnectionInfo;
import ljas.commons.network.TaskSender;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.task.TaskResult;
import ljas.commons.tasking.task.TaskState;
import org.apache.log4j.Logger;


public class TaskQueue {
	private WorkerController		_controller;
	public final int				WORKER_DELAY	= 5;
	private Queue<Task>				_taskQueue;
	private static long				TASKIDCOUNTER	= 0;
	private TaskQueueConfiguration	_configuration;
	private boolean					_initialized;

	public TaskSender getLocal() {
		return getConfiguration().getLocal();
	}

	public Logger getLogger() {
		return getLocal().getLogger();
	}

	public WorkerController getController() {
		return _controller;
	}

	private void setWorkerController(WorkerController controller) {
		_controller = controller;
	}

	public boolean isInitialized() {
		return _initialized;
	}

	private void setInitialized(boolean initialized) {
		_initialized = initialized;
	}

	private Queue<Task> getQueue() {
		return _taskQueue;
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
	public void addTask(Task task) {
		if (task.getState() != TaskState.FINISHED && task.getState() != TaskState.NEW) {
			getLogger().debug("Added task '" + task + "' to queue");

			getQueue().add(task);
		}
	}

	public Task removeTask() {
		return getQueue().remove();
	}

	public void addBackgroundTask(Task task) {
		task.setLocal(getLocal());
		_controller.addBackgroundWorker(task);
	}

	public TaskQueue(TaskQueueConfiguration configuration) {
		setConfiguration(configuration);
		setWorkerController(new WorkerController(this));
		setInitialized(false);
	}

	public void activate(Task... backgroundTasks) {
		if (!isInitialized()) {
			initialize();
		}

		// Add background tasks
		for (Task task : backgroundTasks) {
			_controller.addBackgroundWorker(task);
		}

		// Start Workers
		_controller.start();
	}

	/**
	 * Prepares a task for performing
	 *
	 * @param task
	 *            The task to prepare
	 * @return The prepared task
	 */
	private Task prepareTask(Task task) {
		// Set local to null. Otherwise NotSerializableException
		task.setLocal(null);

		if (task.getState() == TaskState.NEW) {
			// Complete TaskHeader
			task.setApplicationId(getLocal().getApplication().getApplicationId());
			task.setSenderInfo(getLocal().getLocalConnectionInfo());

			// Set state
			task.setState(TaskState.DO_PERFORM);
		}

		return task;
	}

	public void executeTaskRemote(Task task, ConnectionInfo connectionInfo) throws Exception {
		getLocal().getTaskReceiver(connectionInfo).writeObject(prepareTask(task));
	}

	public void executeTaskLocal(Task task) throws Exception {
		addTask(prepareTask(task));
	}

	public static long createTaskId() {
		return ++TASKIDCOUNTER;
	}

	/**
	 * Deactivates the {@link TaskQueue}
	 */
	public void deactivate() {
		getController().killAll();
		setInitialized(false);
	}

	/**
	 * Initialize the {@link TaskQueue} to its initial state
	 */
	private void initialize() {
		setWorkerController(new WorkerController(this));
		_taskQueue = new ConcurrentLinkedQueue<Task>();
		setInitialized(true);
	}

	/**
	 * <ol>
	 * <li>Checks a task. If the task is not correct (not same applId or
	 * applVersion) it will automatically be sent back with an error.</li>
	 * <li>Checks whether the system is overloaded</li>
	 * </ol>
	 *
	 * @param task
	 *            The task to be checked
	 * @return True when the task is all right
	 * @throws Exception
	 */
	public boolean checkTask(Task task) throws Exception {
		/*
		 * System is overloaded
		 */
		if (getConfiguration().getMaximumTaskCount() > 0
				&& getQueue().size() >= getConfiguration().getMaximumTaskCount()) {
			// TODO: Print an error

			task.setResult(TaskResult.ERROR);
			task.setState(TaskState.DO_CHECK);
			task.setResultMessage(Task.MSG_SYSTEM_OVERLOAD);
			executeTaskRemote(task, task.getSenderInfo());

			return false;
		}

		/*
		 * Wrong application id, do not execute due to safety concerns
		 */
		if (getLocal().getApplication().getApplicationId() != task.getApplicationId()
				&& task.getState() == TaskState.DO_PERFORM) {
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
