package ljas.commons.worker;

import java.util.NoSuchElementException;

import ljas.commons.network.ConnectionInfo;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.task.TaskResult;
import ljas.commons.tasking.task.TaskState;
import ljas.commons.tasking.taskqueue.WorkerController;

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
			Task task = getController().getTaskQueue().removeTask();
			task.setLocal(getController().getTaskQueue().getLocal());

			if (task.getState() == TaskState.DO_PERFORM) {
				performTask(task);
			}

			else if (task.getState() == TaskState.DO_CHECK) {
				checkTask(task);
			}

		} catch (NoSuchElementException e) {
			// nothing
		} catch (NullPointerException e) {
			Thread.sleep(getController().getTaskQueue().getWorkerDelay());
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
		getController().getTaskQueue().getLogger()
				.debug("Task '" + task + "' checked");
	}

	private void performTask(Task task) throws Exception {
		// Only perform task when the result has not been set previously
		if (task.getResult() == TaskResult.NONE) {
			// Perform the task
			task.perform();

			// Log
			getController().getTaskQueue().getLogger()
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
			ConnectionInfo remote = getController().getTaskQueue().getLocal()
					.getTaskReceiver(task.getSenderInfo())
					.getConnectionInfo();
			ConnectionInfo local = getController().getTaskQueue().getLocal().getLocalConnectionInfo();
			
			if(remote.equals(local)){
				// Local task
				getController().getTaskQueue().executeTaskLocal(task);
			}else{
				// Remote task
				getController().getTaskQueue().executeTaskRemote(task, remote);
			}
			

			// Log
			getController().getTaskQueue().getLogger()
					.debug("Task '" + task + "' sent back to sender");
		} catch (Exception e) {
			// Task receiver not found, probably disconnected
			// We do not have to keep the task longer

			// do not log
		}
	}
}
