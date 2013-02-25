package ljas.commons.tasking.observation;

import ljas.commons.tasking.Task;

/**
 * TODO Notify with warnings!
 * 
 * @author jonashansen
 * 
 */
public interface TaskObserver {
	public void notifyExecuted(Task task);

	public void notifyFail(Task task);

	public void notifySuccess(Task task);
}
