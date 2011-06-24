package ljas.commons.worker;

import java.util.NoSuchElementException;

import ljas.commons.network.ConnectionInfo;
import ljas.commons.tasking.sendable.task.Task;
import ljas.commons.tasking.sendable.task.TaskResult;
import ljas.commons.tasking.sendable.task.TaskState;
import ljas.commons.tasking.taskspool.WorkerController;


public class TaskWorker extends Worker {

	public TaskWorker(WorkerController controller) {
		super(controller, Thread.NORM_PRIORITY, false);
		setName("TaskWorker");
	}

	@Override
	public void runItOnce() throws Exception {
		try {
			Task task = getController().getTaskQueue().remove();
			task.setLocal(getController().getTaskspool().getLocal());

			if (task.getState() == TaskState.DO_PERFORM) {
				taskPerform(task);
			}

			else if (task.getState() == TaskState.DO_CHECK) {
				taskCheck(task);
			}

		} catch (NoSuchElementException e) {
			// nothing
		} catch (NullPointerException e) {
			Thread.sleep(getController().getTaskspool().getWorkerDelay());
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
		}
	}

	private void taskCheck(Task task) throws Exception {
		// Task has been performed by remote
		task.postPerform();
		task.setState(TaskState.FINISHED);
		//
		task.notifyAllExecuted();
	}

	private void taskPerform(Task task) {
		// Only perform task when the result has not been set previously
		if (task.getResult() == TaskResult.NONE) {
			task.perform();
		}

		// Set result (ignore, when set before)
		if (task.getResult() == TaskResult.NONE) {
			task.setResult(TaskResult.SUCCESSFUL);
		}

		// Set state
		task.setState(TaskState.DO_CHECK);

		// Send the task back to its sender
		try {
			ConnectionInfo remote = getController().getTaskspool().getLocal()
					.getTaskReceiver(task.getHeader().getSenderInfo())
					.getConnectionInfo();

			getController().getTaskspool().sendTask(task, remote);
		} catch (Exception e) {
			// Task receiver not found, probably disconnected
			// We do not have to keep the task longer

			// do not log
		}
	}
}
