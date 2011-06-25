package ljas.commons.tasking.taskspool;

import ljas.commons.network.ConnectionInfo;
import ljas.commons.network.SendsTasks;
import ljas.commons.tasking.sendable.task.Task;
import ljas.commons.tasking.sendable.task.TaskResult;
import ljas.commons.tasking.sendable.task.TaskState;
import ljas.commons.tools.AutoVariable;
import ljas.commons.worker.Worker;

import org.apache.log4j.Logger;

public class TaskSpool {
	// MEMBERS
	private final WorkerController _controller;
	private final int _workerDelay;
	private SendsTasks _local;
	private final int _maximumTasks;
	private final AutoVariable<Boolean> _autoWarnSystemOverloaded;
	private static long TASKIDCOUNTER = 0;

	// GETTERS & SETTERS
	public int getWorkerDelay() {
		return _workerDelay;
	}

	private void setLocal(SendsTasks value) {
		_local = value;
	}

	public SendsTasks getLocal() {
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

	// CONSTRUCTORS
	public TaskSpool(int taskWorkers, int socketWorkers, SendsTasks local,
			int maximumTasks) {
		// Initialize
		setLocal(local);
		_maximumTasks = maximumTasks;
		_workerDelay = 10;
		_controller = new WorkerController(this, taskWorkers, socketWorkers);
		_autoWarnSystemOverloaded = new AutoVariable<Boolean>(true, 5000);
	}

	// METHODS
	public void activate() {
		// Start Workers
		_controller.start();

		// Print some info
		getLogger().debug("Taskspool is running");
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
			task.getHeader().setApplicationId(
					getLocal().getApplication().getApplicationId());
			task.getHeader().setSenderInfo(getLocal().getLocalConnectionInfo());

			// Set state
			task.setState(TaskState.DO_PERFORM);
		}

		return task;
	}

	public void remoteTask(Task task, ConnectionInfo connectionInfo)
			throws Exception {
		getLocal().getTaskReceiver(connectionInfo).writeObject(
				prepareTask(task));
	}

	public void localTask(Task task) throws Exception {
		getController().addTask(prepareTask(task));
	}
	

	public static long createTaskId() {
		return ++TASKIDCOUNTER;
	}

	public void addBackgroundTask(Task task) {
		task.setLocal(getLocal());
		_controller.addBackgroundWorker(task);
	}

	// public synchronized Notification getNotification()
	// throws TaskNotFoundException {
	// if (getNotificationQueue().size() <= 0) {
	// throw new TaskNotFoundException(0);
	// }
	//
	// Notification n = null;
	// synchronized (getNotificationQueue()) {
	// n = getNotificationQueue().get(0);
	// getNotificationQueue().remove(0);
	// }
	// return n;
	// }

	public void deactivate() {
		for (Worker w : getController().getWorkers()) {
			w.kill();
		}

		_autoWarnSystemOverloaded.interrupt();
	}

	/**
	 * <ol>
	 * <li>Checks a task. If the task is not correct it will automatically will be
	 * send back with an error.</li>
	 * <li>Checks whether the system is overloaded</li>
	 * </ol>
	 * @param task
	 *            The task to be checked
	 * @return True when the task is all right
	 * @throws Exception
	 */
	public boolean checkTask(Task task) throws Exception {
		/*
		 * System is overloaded
		 */
		if (getMaximumTasks() > 0
				&& getController().getTaskQueueSize() >= getMaximumTasks()) {
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
			remoteTask(task, task.getHeader().getSenderInfo());

			return false;
		}

		/*
		 * Wrong application id, do not execute due to safety concerns
		 */
		if (getLocal().getApplication().getApplicationId() != task.getHeader()
				.getApplicationId() && task.getState() == TaskState.DO_PERFORM) {
			getLogger().warn(Task.MSG_SAFETY_CONCERN);

			task.setResult(TaskResult.ERROR);
			task.setState(TaskState.DO_CHECK);
			task.setResultMessage(Task.MSG_SAFETY_CONCERN);
			remoteTask(task, task.getHeader().getSenderInfo());

			return false;
		}
		return true;
	}
}
