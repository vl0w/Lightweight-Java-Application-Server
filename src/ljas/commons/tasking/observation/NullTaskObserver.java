package ljas.commons.tasking.observation;

import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;

public class NullTaskObserver implements TaskObserver {

	@Override
	public void notifyExecuted(Task task) {
		// nothing
	}

	@Override
	public void notifyExecutedWithErrors(Task task,
			List<TaskException> exceptions) {
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
