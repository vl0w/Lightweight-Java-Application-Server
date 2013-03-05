package ljas.commons.tasking.observation;

import ljas.commons.tasking.Task;

public class NullTaskObserver implements TaskObserver {

	@Override
	public void notifyExecuted(Task task) {
		// nothing
	}

	@Override
	public void notifyExecutedWithErrors(Task task) {
		// nothing
	}

	@Override
	public void notifyExecutedWithSuccess(Task task) {
		// nothing
	}

	@Override
	public void notifyExecutedWithWarnings(Task task) {
		// nothing
	}

}
