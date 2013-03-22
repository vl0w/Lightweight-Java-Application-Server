package ljas.commons.tasking.observation;

import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;

/**
 * 
 * @author jonashansen
 * 
 */
public interface TaskObserver<V extends Task> {
	void notifyExecuted(V task);

	void notifyExecutedWithErrors(V task, List<TaskException> exceptions);

	void notifyExecutedWithSuccess(V task);

	void notifyExecutedWithWarnings(V task);
}
