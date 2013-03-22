package ljas.commons.tasking.observation;

import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;

public class NullTaskObserver<V extends Task> implements TaskObserver<V> {

	@Override
	public void notifyExecuted(V task) {
		// nothing
	}

	@Override
	public void notifyExecutedWithErrors(V task, List<TaskException> exceptions) {
		// nothing
	}

	@Override
	public void notifyExecutedWithSuccess(V task) {
		// nothing
	}

	@Override
	public void notifyExecutedWithWarnings(V task) {
		// nothing
	}

}
