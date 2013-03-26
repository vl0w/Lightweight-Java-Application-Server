package ljas.commons.tasking.observation;

import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.step.TaskStep;
import ljas.commons.tasking.step.impl.FinishTaskStep;

/**
 * This class gets triggered upon completion of a {@link Task} object. It
 * strongly depends on the overall {@link TaskStepResult} calculated by each
 * result of each {@link TaskStep}.
 * 
 * <br />
 * <br />
 * 
 * The class gets triggered by the {@link FinishTaskStep}, the last step of
 * every Task(-flow). This step gets automatically attached to every Task in the
 * building process of the TaskFlow.
 * 
 * @author jonashansen
 * @see FinishTaskStep
 * @see TaskStepResult
 * 
 */
public interface TaskObserver<V extends Task> {
	/**
	 * When a task has been executed with any result.
	 * 
	 * @param task
	 *            The executed task
	 */
	void notifyExecuted(V task);

	/**
	 * When a task has been executed with errors.
	 * 
	 * @param task
	 *            The executed task
	 */
	void notifyExecutedWithErrors(V task, List<TaskException> exceptions);

	/**
	 * When a task has been executed with <b>no</b> errors.
	 * 
	 * @param task
	 *            The executed task
	 */
	void notifyExecutedWithSuccess(V task);

	/**
	 * When a task has been executed and has warnings.
	 * 
	 * @param task
	 *            The executed task
	 */
	void notifyExecutedWithWarnings(V task);
}
