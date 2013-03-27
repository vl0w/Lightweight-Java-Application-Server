package ljas.commons.tasking.observation;

import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;

/**
 * 
 * @author jonashansen
 * 
 */
public interface TaskObserver<T extends Task> {
	void notifyExecuted(T task);

	void notifyExecutedWithErrors(T task, List<TaskException> exceptions);

	void notifyExecutedWithSuccess(T task);

	void notifyExecutedWithWarnings(T task);
}
