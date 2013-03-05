package ljas.commons.tasking.observation;

import ljas.commons.tasking.Task;

/**
 * TODO Notify with warnings!
 * 
 * @author jonashansen
 * 
 */
public interface TaskObserver {
	void notifyExecuted(Task task);

	void notifyFail(Task task);

	void notifySuccess(Task task);
}
