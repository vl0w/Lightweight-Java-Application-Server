package ljas.commons.tasking.status;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.TaskStateResult;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.status.navigator.TaskNavigator;

public interface TaskState {
	/**
	 * The logic to perform in the current {@link TaskState}
	 */
	void execute() throws TaskException;

	/**
	 * Sets the {@link TaskSystem} for this {@link TaskState}
	 * 
	 * @param taskSystem
	 */
	void setTaskSystem(TaskSystem taskSystem);

	TaskSystem getTaskSystem();

	TaskStateResult getResult();

	void setResult(TaskStateResult result);

	/**
	 * Returns the {@link TaskNavigator} of this {@link TaskState}
	 * 
	 * @see TaskNavigator
	 */
	TaskNavigator getNavigator();
}
