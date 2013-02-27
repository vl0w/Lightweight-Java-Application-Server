package ljas.commons.tasking.environment.observation;

import ljas.commons.tasking.Task;

@Deprecated
public interface TaskSystemObserver {
	public void notifyTaskExecuted(Task task);
}
