package ljas.commons.tasking.observation;

import ljas.commons.tasking.Task;

/**
 * Used to overwrite just the events that you want!
 * 
 * @author jonashansen
 * 
 */
public class TaskObserverAdapter implements TaskObserver {

	@Override
	public void notifyExecuted(Task task) {
		// nothing
	}

	@Override
	public void notifyFail(Task task) {
		// nothing
	}

	@Override
	public void notifySuccess(Task task) {
		// nothing
	}

}
