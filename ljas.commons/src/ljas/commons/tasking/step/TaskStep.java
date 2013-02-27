package ljas.commons.tasking.step;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.environment.TaskSystem;

public interface TaskStep {
	/**
	 * The logic to perform in the current {@link TaskStep}
	 */
	void execute() throws TaskException;

	/**
	 * Sets the {@link TaskSystem} for this {@link TaskStep}
	 * 
	 * @param taskSystem
	 */
	void setTaskSystem(TaskSystem taskSystem);

	TaskSystem getTaskSystem();

	TaskStepResult getResult();

	void setResult(TaskStepResult result);

	boolean isForNavigation();
}
