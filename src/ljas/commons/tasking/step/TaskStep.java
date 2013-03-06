package ljas.commons.tasking.step;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;
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
	void execute(ExecutingContext context) throws TaskException;

	TaskStepResult getResult();

	void setResult(TaskStepResult result);

	boolean isForNavigation();

	void setException(TaskException exception);

	TaskException getException();
}
