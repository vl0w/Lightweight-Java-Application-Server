package ljas.commons.tasking.status.navigator;

import ljas.commons.tasking.Task;
import ljas.commons.tasking.status.TaskState;

public interface TaskNavigator {
	/**
	 * Navigates the task to the system which will execute the logic of it's
	 * state
	 * 
	 * @see {@link TaskState#execute()}
	 */
	void navigate(Task task);
}
