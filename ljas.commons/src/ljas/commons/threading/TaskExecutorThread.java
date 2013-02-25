package ljas.commons.threading;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStateResult;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.status.TaskState;
import ljas.commons.tasking.status.impl.FinishedState;

public class TaskExecutorThread extends RepetitiveThread {
	private final static int IDLE_TIME_TO_SELF_DESTRUCTION = 10000;
	private Queue<Task> taskQueue;
	private TaskSystem taskSystem;
	private int idleCycleCounter;

	public TaskExecutorThread(ThreadSystem threadSystem, TaskSystem taskSystem) {
		super(threadSystem);
		setName("TaskExecutor");
		this.taskSystem = taskSystem;
		this.taskQueue = new ConcurrentLinkedQueue<Task>();
		this.idleCycleCounter = 0;
		start();
	}

	public Queue<Task> getTaskQueue() {
		return taskQueue;
	}

	@Override
	public void runCycle() throws Exception {
		try {
			Task task = getTaskQueue().remove();

			long startTime = System.currentTimeMillis();

			executeCurrentState(task);
			navigateSuccessor(task);

			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			taskSystem.getTaskMonitor().monitorTaskTime(task, duration);

		} catch (NoSuchElementException e) {
			workerIsIdleCycle();
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
			return getTaskQueue().add(task);
		}
		return false;
	}

	private void executeCurrentState(Task task) {
		TaskState state = getCurrentState(task);
		try {
			state.setTaskSystem(taskSystem);
			state.execute();

			if (state.getResult() == TaskStateResult.NONE) {
				state.setResult(TaskStateResult.SUCCESS);
			}
		} catch (TaskException e) {
			state.setResult(TaskStateResult.ERROR);
			task.setResultMessage(e.getMessage());
		}
		task.getStateHistory().add(state);
	}

	private TaskState getCurrentState(Task task) {
		TaskState currentState = task.getCurrentState();

		// Initial state
		if (currentState == null) {
			currentState = task.getStatusFactory().nextStatus(task,
					currentState);
		}

		return currentState;
	}

	private void navigateSuccessor(Task task) {
		TaskState currentStatus = getCurrentState(task);

		if (!currentStatus.getClass().equals(FinishedState.class)) {
			TaskState successorStatus = task.getStatusFactory().nextStatus(
					task, currentStatus);
			successorStatus.setTaskSystem(taskSystem);
			task.setCurrentStatus(successorStatus);
			successorStatus.getNavigator().navigate(task);
		}
	}

	private void workerIsIdleCycle() {
		final int workerDelay = ThreadSystem.WORKER_DELAY;
		sleepSilent(workerDelay);
		idleCycleCounter++;
		// Must destroy itself?
		if (idleCycleCounter * workerDelay >= IDLE_TIME_TO_SELF_DESTRUCTION) {
			getLogger().debug(
					"TaskWorker has not been used since "
							+ IDLE_TIME_TO_SELF_DESTRUCTION
							+ "ms. Shuting it down.");
			kill();
		}
	}

	/**
	 * Sleeps and swallows every {@link InterruptedException}
	 * 
	 * @param millis
	 */
	private void sleepSilent(long millis) {
		try {
			sleep(millis);
		} catch (InterruptedException e) {
			// nothing
		}
	}

}
