package ljas.commons.worker;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ljas.commons.network.ConnectionInfo;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.task.TaskResult;
import ljas.commons.tasking.task.TaskState;

public class TaskWorker extends Worker {
	private final static int IDLE_TIME_TO_SELF_DESTRUCTION = 10000;
	private Queue<Task> _taskQueue;
	private int _queueEmptyCounter;

	public TaskWorker(WorkerController controller) {
		this(controller, 0);
	}

	public TaskWorker(WorkerController controller, int id) {
		super(controller, Thread.NORM_PRIORITY, false);
		_taskQueue = new ConcurrentLinkedQueue<Task>();
		int port = controller.getTaskController().getLocal()
				.getLocalConnectionInfo().getPort();
		setName("TaskWorker " + id + " (:" + port + ")");
		_queueEmptyCounter = 0;
	}

	public synchronized Queue<Task> getTaskQueue() {
		return _taskQueue;
	}

	@Override
	public void runItOnce() throws Exception {
		try {
			Task task = getTaskQueue().remove();
			task.setLocal(getWorkerController().getTaskController().getLocal());

			if (task.getState() == TaskState.DO_PERFORM) {
				performTask(task);
			}

			else if (task.getState() == TaskState.DO_CHECK) {
				checkTask(task);
			}

		} catch (NoSuchElementException e) {
			final int workerDelay = getWorkerController().getTaskController().WORKER_DELAY;
			sleep(workerDelay);
			_queueEmptyCounter++;
			// Must destroy itself?
			if (_queueEmptyCounter * workerDelay >= IDLE_TIME_TO_SELF_DESTRUCTION) {
				getLogger().info(
						"TaskWorker has not been used since "
								+ IDLE_TIME_TO_SELF_DESTRUCTION
								+ "ms. Shuting it down.");
				kill();
			}
		} catch (Exception e) {
			getLogger().error(e);
		}
	}

	/**
	 * Schedules the task
	 * 
	 * @param task
	 *            The task to schedule and execute
	 * @return True when the task has succesfully been scheduled for execution.
	 */
	public boolean scheduleTask(Task task) {
		if (!isKilled()) {
			getTaskQueue().add(task);
			return true;
		}
		return false;
	}

	private void checkTask(Task task) throws Exception {
		// Task has been performed by remote
		task.postPerform();
		task.setState(TaskState.FINISHED);

		// Notify all observers
		task.notifyAllExecuted();

		// Log
		getWorkerController().getTaskController().getLogger()
				.debug("Task '" + task + "' checked");
	}

	private void performTask(Task task) throws Exception {
		// Get time
		long startTime = System.currentTimeMillis();

		// Only perform task when the result has not been set previously
		if (task.getResult() == TaskResult.NONE) {
			// Perform the task
			task.perform();

			// Log
			getWorkerController().getTaskController().getLogger()
					.debug("Executed task '" + task + "'");
		}

		// Set result (ignore, when set before)
		if (task.getResult() == TaskResult.NONE) {
			task.setResult(TaskResult.SUCCESSFUL);
		}

		// Set state
		task.setState(TaskState.DO_CHECK);

		// Send the task back to its sender, else check
		try {
			ConnectionInfo remote = getWorkerController().getTaskController()
					.getLocal().getTaskReceiver(task.getSenderInfo())
					.getConnectionInfo();
			ConnectionInfo local = getWorkerController().getTaskController()
					.getLocal().getLocalConnectionInfo();

			if (remote.equals(local)) {
				// Local task
				getWorkerController().getTaskController()
						.executeTaskLocal(task);
			} else {
				// Remote task
				getWorkerController().getTaskController().executeTaskRemote(
						task, remote);
			}

			// Log
			getWorkerController().getTaskController().getLogger()
					.debug("Task '" + task + "' sent back to sender");
		} catch (Exception e) {
			// Task receiver not found, probably disconnected
			// We do not have to keep the task longer

			// do not log
		}

		// Get time, notify the TaskController about it
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		getWorkerController().getTaskController().storeExecutionTime(task,
				duration);
	}

	@Override
	public void kill() {
		getWorkerController().getWorkers().remove(this);
		super.kill();
	}
}
