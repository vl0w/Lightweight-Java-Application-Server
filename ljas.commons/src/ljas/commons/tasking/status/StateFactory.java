package ljas.commons.tasking.status;

import java.io.Serializable;

import ljas.commons.tasking.Task;
import ljas.commons.tasking.status.impl.FinishedState;

/**
 * An abstract interface which creates successors {@link TaskState}s. When no
 * successor exists, simply return {@link FinishedState}
 * 
 * @author jonashansen
 * @see FinishedState
 * @see TaskState
 * 
 */
public interface StateFactory extends Serializable {
	/**
	 * Returns a successor for a {@link TaskState}. Return {@link FinishedState}
	 * when no successor exists.
	 * 
	 * @param task
	 *            The {@link Task}
	 * @param currentStatus
	 *            The current {@link TaskState} which has been executed
	 * @return The successor of the current {@link TaskState}
	 * @see FinishedState
	 */
	TaskState nextStatus(Task task, TaskState currentStatus);
}
