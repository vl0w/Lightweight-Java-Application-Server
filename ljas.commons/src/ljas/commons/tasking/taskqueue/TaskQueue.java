package ljas.commons.tasking.taskqueue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ljas.commons.network.ConnectionInfo;
import ljas.commons.network.TaskSender;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.task.TaskResult;
import ljas.commons.tasking.task.TaskState;
import ljas.commons.tools.AutoVariable;
import ljas.commons.worker.Worker;

import org.apache.log4j.Logger;

public class TaskQueue {
	private final WorkerController _controller;
	private final int _workerDelay;
	private TaskSender _local;
	private final int _maximumTasks;
	private final AutoVariable<Boolean> _autoWarnSystemOverloaded;
	private Queue<Task> _taskQueue;
	private static long TASKIDCOUNTER = 0;

	public int getWorkerDelay() {
		return _workerDelay;
	}

	public TaskSender getLocal() {
		return _local;
	}

	public Logger getLogger() {
		return getLocal().getLogger();
	}

	public WorkerController getController() {
		return _controller;
	}

	public int getMaximumTasks() {
		return _maximumTasks;
	}

	private Queue<Task> getQueue() {
		return _taskQueue;
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

	public TaskQueue(int taskWorkers, int socketWorkers, TaskSender local,
			int maximumTasks) {
		_local = local;
		_taskQueue = new ConcurrentLinkedQueue<Task>();
		_maximumTasks = maximumTasks;
		_workerDelay = 10;
		_controller = new WorkerController(this, taskWorkers, socketWorkers);
		_autoWarnSystemOverloaded = new AutoVariable<Boolean>(true, 5000);
	}

	public void activate() {
		// Start Workers
		_controller.start();

		// Print some info
		getLogger().debug("Taskqueue is running");
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
			task.setApplicationId(getLocal().getApplication()
					.getApplicationId());
			task.setSenderInfo(getLocal().getLocalConnectionInfo());

			// Set state
			task.setState(TaskState.DO_PERFORM);
		}

		return task;
	}

	public void executeTaskRemote(Task task, ConnectionInfo connectionInfo)
			throws Exception {
		getLocal().getTaskReceiver(connectionInfo).writeObject(
				prepareTask(task));
	}

	public void executeTaskLocal(Task task) throws Exception {
		addTask(prepareTask(task));
	}

	public static long createTaskId() {
		return ++TASKIDCOUNTER;
	}

	public void deactivate() {
		for (Worker w : getController().getWorkers()) {
			w.kill();
		}

		_autoWarnSystemOverloaded.interrupt();
	}

	/**
	 * <ol>
	 * <li>Checks a task. If the task is not correct (not same applId or applVersion) it will automatically 
	 * be sent back with an error.</li>
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
		if (getMaximumTasks() > 0 && getQueue().size() >= getMaximumTasks()) {
			// Warn!
			synchronized (this) {
				if (_autoWarnSystemOverloaded.getValue()) {
					getLogger().warn(
							"Server overloaded! ("
									+ _autoWarnSystemOverloaded
											.getAccessCount()
									+ " tasks refused)");
					_autoWarnSystemOverloaded.setValue(false);
				}
			}

			task.setResult(TaskResult.ERROR);
			task.setState(TaskState.DO_CHECK);
			task.setResultMessage(Task.MSG_SYSTEM_OVERLOAD);
			executeTaskRemote(task, task.getSenderInfo());

			return false;
		}

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
