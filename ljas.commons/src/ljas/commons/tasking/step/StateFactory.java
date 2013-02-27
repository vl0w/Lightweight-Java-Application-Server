package ljas.commons.tasking.step;

import java.io.Serializable;

import ljas.commons.tasking.Task;
import ljas.commons.tasking.step.impl.FinishTaskStep;

/**
 * An abstract interface which creates successors {@link TaskStep}s. When no
 * successor exists, simply return {@link FinishTaskStep}
 * 
 * @author jonashansen
 * @see FinishTaskStep
 * @see TaskStep
 * 
 */
@Deprecated
public interface StateFactory extends Serializable {
	/**
	 * Returns a successor for a {@link TaskStep}. Return {@link FinishTaskStep}
	 * when no successor exists.
	 * 
	 * @param task
	 *            The {@link Task}
	 * @param currentStatus
	 *            The current {@link TaskStep} which has been executed
	 * @return The successor of the current {@link TaskStep}
	 * @see FinishTaskStep
	 */
	TaskStep nextStatus(Task task, TaskStep currentStatus);
}
