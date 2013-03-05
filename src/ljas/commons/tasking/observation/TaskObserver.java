package ljas.commons.tasking.observation;

import ljas.commons.tasking.Task;

/**
 * 
 * @author jonashansen
 * 
 */
public interface TaskObserver {
	void notifyExecuted(Task task);

	void notifyExecutedWithErrors(Task task);

	void notifyExecutedWithSuccess(Task task);

	void notifyExecutedWithWarnings(Task task);
}
