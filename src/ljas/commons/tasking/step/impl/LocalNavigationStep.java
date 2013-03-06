package ljas.commons.tasking.step.impl;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.step.ExecutingContext;
import ljas.commons.tasking.step.NavigationStep;

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
