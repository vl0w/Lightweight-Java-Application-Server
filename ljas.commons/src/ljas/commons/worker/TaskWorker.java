package ljas.commons.worker;

import java.util.NoSuchElementException;

import ljas.commons.network.ConnectionInfo;
import ljas.commons.tasking.sendable.task.Task;
import ljas.commons.tasking.sendable.task.TaskResult;
import ljas.commons.tasking.sendable.task.TaskState;
import ljas.commons.tasking.taskspool.WorkerController;

public class TaskWorker extends Worker {

	public TaskWorker(WorkerController controller) {
		this(controller, "TaskWorker ?");
	}

	public TaskWorker(WorkerController controller, String threadName) {
		super(controller, Thread.NORM_PRIORITY, false);
		setName(threadName);
	}

	@Override
	public void runItOnce() throws Exception {
		try {
			Task task = getController().removeTask();
			task.setLocal(getController().getTaskspool().getLocal());

			if (task.getState() == TaskState.DO_PERFORM) {
				performTask(task);
			}

			else if (task.getState() == TaskState.DO_CHECK) {
				checkTask(task);
			}

		} catch (NoSuchElementException e) {
			// nothing
		} catch (NullPointerException e) {
			Thread.sleep(getController().getTaskspool().getWorkerDelay());
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	private void checkTask(Task task) throws Exception {
		// Task has been performed by remote
		task.postPerform();
		task.setState(TaskState.FINISHED);

		// Notify all observers
		task.notifyAllExecuted();

		// Log
		getController().getTaskspool().getLogger()
				.debug("Task '" + task + "' checked");
	}

	private void performTask(Task task) throws Exception {
		// Only perform task when the result has not been set previously
		if (task.getResult() == TaskResult.NONE) {
			// Perform the task
			task.perform();

			// Log
			getController().getTaskspool().getLogger()
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
			ConnectionInfo remote = getController().getTaskspool().getLocal()
					.getTaskReceiver(task.getHeader().getSenderInfo())
					.getConnectionInfo();
			ConnectionInfo local = getController().getTaskspool().getLocal().getLocalConnectionInfo();
			
			if(remote.equals(local)){
				// Local task
				getController().getTaskspool().localTask(task);
			}else{
				// Remote task
				getController().getTaskspool().remoteTask(task, remote);
			}
			

			// Log
			getController().getTaskspool().getLogger()
					.debug("Task '" + task + "' sent back to sender");
		} catch (Exception e) {
			// Task receiver not found, probably disconnected
			// We do not have to keep the task longer

			// do not log
		}
	}
}
