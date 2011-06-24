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
	public TaskSpool(int taskWorkers, int socketWorkers,
			int notificationWorkers, SendsTasks local, int maximumTasks) {
		// Initialize
		setLocal(local);
		_maximumTasks = maximumTasks;
		_workerDelay = 10;
		_controller = new WorkerController(this, taskWorkers, socketWorkers,
				notificationWorkers);
		_autoWarnSystemOverloaded = new AutoVariable<Boolean>(true, 5000);
	}

	// METHODS
	public void activate() {
		// Start Workers
		_controller.start();

		// Print some info
		getLogger().debug("Taskspool is running");
	}

	public void sendTask(Task task, ConnectionInfo connectionInfo) throws Exception {
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

		getLocal().getTaskReceiver(connectionInfo).writeObject(task);
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
	 * Checks a task. If the task is not correct it will automatically will be
	 * send back with an error.
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
		if (getMaximumTasks() > 0
				&& getController().getTaskQueue().size() >= getMaximumTasks()) {
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
			sendTask(task, task.getHeader().getSenderInfo());

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
			sendTask(task, task.getHeader().getSenderInfo());

			return false;
		}
		return true;
	}
}
