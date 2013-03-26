package ljas.commons.tasking;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.executors.TaskThread;
import ljas.commons.tasking.step.ExecutingContext;
import ljas.commons.tasking.step.TaskStep;

/**
 * This class executes what needs to be executed for a {@link Task}. It
 * implements the {@link Runnable} interface as this object will probably be
 * delegated to a thread service.
 * 
 * @author jonashansen
 * 
 */
public class TaskRunnable implements Runnable {

	private Task task;
	private TaskSystem taskSystem;

	public TaskRunnable(Task task, TaskSystem taskSystem) {
		this.task = task;
		this.taskSystem = taskSystem;
	}

	@Override
	public void run() {
		TaskStep step;
		do {
			step = task.getTaskFlow().nextStep();
			executeStep(task, step);
		} while (!step.isForNavigation());
	}

	private void executeStep(Task task, TaskStep step) {
		ExecutingContext context = new ExecutingContext(taskSystem, task);

		TaskThread thread = (TaskThread) Thread.currentThread();
		thread.setExecutingContext(context);

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

}
