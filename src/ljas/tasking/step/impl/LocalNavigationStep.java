package ljas.tasking.step.impl;

import ljas.exception.TaskException;
import ljas.tasking.Task;
import ljas.tasking.environment.TaskSystem;
import ljas.tasking.step.ExecutingContext;
import ljas.tasking.step.NavigationStep;

/**
 * Navigates the task to the local {@link TaskSystem}
 * 
 * @author jonashansen
 * 
 */
public class LocalNavigationStep extends NavigationStep {

	private static final long serialVersionUID = -8517022795716997563L;
	private Task task;

	public LocalNavigationStep(Task task) {
		this.task = task;
	}

	@Override
	public void execute(ExecutingContext context) throws TaskException {
		context.getTaskSystem().scheduleTask(task);
	}

}
