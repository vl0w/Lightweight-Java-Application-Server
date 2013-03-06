package ljas.commons.threading;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.tasking.step.ExecutingContext;
import ljas.commons.tasking.step.TaskStep;

public class TaskExecutorThread extends RepetitiveThread {
	private Queue<Task> taskQueue;
	private TaskSystem taskSystem;

	public TaskExecutorThread(ThreadSystem threadSystem, TaskSystem taskSystem) {
		super(threadSystem);
		this.taskSystem = taskSystem;
		this.taskQueue = new ConcurrentLinkedQueue<>();
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
			TaskMonitor taskMonitor = taskSystem.getTaskMonitor();
			taskMonitor.monitorTaskTime(task, duration);
			getThreadSystem().getThreadFactory()
					.updateTaskExecutorsByMonitoring(taskMonitor);

		} catch (NoSuchElementException e) {
			final int workerDelay = getThreadSystem().getDefaultThreadDelay();
			sleepSilent(workerDelay);
		} catch (Exception e) {
			getLogger().error("Unknown exception occured on " + toString(), e);
		}
	}

	private void executeStep(Task task, TaskStep step) {

		ExecutingContext context = getExecutingContext();

		try {
			step.execute(context);
		} catch (TaskException e) {
			step.setResult(TaskStepResult.ERROR);
			step.setException(e);
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
		if (isKilled()) {
			return false;
		}
		return getTaskQueue().add(task);
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

	private ExecutingContext getExecutingContext() {
		ExecutingContext context = new ExecutingContext();
		context.setTaskSystem(taskSystem);
		return context;
	}
}
