package ljas.tasking.observation;

import java.util.List;

import ljas.exception.TaskException;
import ljas.tasking.Task;
import ljas.tasking.TaskStepResult;
import ljas.tasking.step.TaskStep;
import ljas.tasking.step.impl.FinishTaskStep;

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
public interface TaskObserver<T extends Task> {
	/**
	 * When a task has been executed with any result.
	 * 
	 * @param task
	 *            The executed task
	 */
	void notifyExecuted(T task);

	/**
	 * When a task has been executed with errors.
	 * 
	 * @param task
	 *            The executed task
	 */
	void notifyExecutedWithErrors(T task, List<TaskException> exceptions);

	/**
	 * When a task has been executed with <b>no</b> errors.
	 * 
	 * @param task
	 *            The executed task
	 */
	void notifyExecutedWithSuccess(T task);

	/**
	 * When a task has been executed and has warnings.
	 * 
	 * @param task
	 *            The executed task
	 */
	void notifyExecutedWithWarnings(T task);

}
