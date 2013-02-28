package ljas.commons.tasking.step;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.flow.TaskFlow;

/**
 * A {@link TaskStep} is the smallest executable part of a {@link Task} and is
 * part of the {@link TaskFlow}.
 * 
 * @author jonashansen
 * @see TaskFlow
 */
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
