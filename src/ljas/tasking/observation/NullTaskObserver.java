package ljas.tasking.observation;

import java.util.List;

import ljas.exception.TaskException;
import ljas.tasking.Task;

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
