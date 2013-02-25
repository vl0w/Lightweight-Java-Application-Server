package ljas.commons.tasking.status.navigator;

import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;

/**
 * Navigates the task to the local {@link TaskSystem}
 * 
 * @author jonashansen
 * 
 */
public class LocalNavigator implements TaskNavigator {

	@Override
	public void navigate(Task task) {
		task.getCurrentState().getTaskSystem().scheduleTask(task);
	}

}
