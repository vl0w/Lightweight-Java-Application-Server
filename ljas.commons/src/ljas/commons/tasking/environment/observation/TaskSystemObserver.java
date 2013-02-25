package ljas.commons.tasking.environment.observation;

import ljas.commons.tasking.Task;

public interface TaskSystemObserver {
	public void notifyTaskExecuted(Task task);
}
