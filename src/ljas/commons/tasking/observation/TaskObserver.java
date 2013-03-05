package ljas.commons.tasking.observation;

import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;

/**
 * 
 * @author jonashansen
 * 
 */
public interface TaskObserver {
	void notifyExecuted(Task task);

	void notifyExecutedWithErrors(Task task, List<TaskException> exceptions);

	void notifyExecutedWithSuccess(Task task);

	void notifyExecutedWithWarnings(Task task);
}
