package ljas.commons.tasking.observation;

import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;

public class NullTaskObserver<T extends Task> implements TaskObserver<T> {

	@Override
	public void notifyExecuted(T task) {
		// nothing
	}

	@Override
	public void notifyExecutedWithErrors(T task, List<TaskException> exceptions) {
		// nothing
	}

	@Override
	public void notifyExecutedWithSuccess(T task) {
		// nothing
	}

	@Override
	public void notifyExecutedWithWarnings(T task) {
		// nothing
	}

}
