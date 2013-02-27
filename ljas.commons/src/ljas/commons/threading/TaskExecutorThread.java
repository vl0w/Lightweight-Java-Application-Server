package ljas.commons.threading;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.step.TaskStep;

public class TaskExecutorThread extends RepetitiveThread {
	private Queue<Task> taskQueue;
	private TaskSystem taskSystem;
	private int idleCycleCounter;

	public TaskExecutorThread(ThreadSystem threadSystem, TaskSystem taskSystem) {
		super(threadSystem);
		setName("TaskExecutor");
		this.taskSystem = taskSystem;
		this.taskQueue = new ConcurrentLinkedQueue<>();
		this.idleCycleCounter = 0;
		start();
	}

	public Queue<Task> getTaskQueue() {
		return taskQueue;
	}

	@Override
	public void runCycle() {
		try {
			Task task = taskQueue.remove();

			long startTime = System.currentTimeMillis();

			TaskStep step;
			do {
				step = task.getTaskFlow().nextStep();
				executeStep(task, step);
			} while (!step.isForNavigation());

			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			taskSystem.getTaskMonitor().monitorTaskTime(task, duration);

		} catch (NoSuchElementException e) {
			workerIsIdleCycle();
		} catch (Exception e) {
			getLogger().error(e);
		}
	}

	private void executeStep(Task task, TaskStep step) {
		step.setTaskSystem(taskSystem);
		try {
			step.execute();
		} catch (TaskException e) {
			step.setResult(TaskStepResult.ERROR);
			task.setResultMessage(e.getMessage());
		}

		if (step.getResult() == TaskStepResult.NONE) {
			step.setResult(TaskStepResult.SUCCESS);
		}

		task.getStepHistory().add(step);
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

	private void workerIsIdleCycle() {
		final int workerDelay = getThreadSystem().getDefaultThreadDelay();
		final long timeForWorkerToSelfDestruction = getThreadSystem()
				.getTimeForTaskExecutorThreadToSelfDestruction();
		sleepSilent(workerDelay);
		idleCycleCounter++;

		// Must destroy itself?
		if (idleCycleCounter * workerDelay >= timeForWorkerToSelfDestruction) {
			getLogger().debug(
					"TaskWorker has not been used since "
							+ timeForWorkerToSelfDestruction
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
