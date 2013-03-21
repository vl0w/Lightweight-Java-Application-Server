package ljas.commons.tasking.step;

import ljas.commons.application.Application;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;

public class ExecutingContext {
	private TaskSystem taskSystem;
	private Task task;

	public ExecutingContext(TaskSystem taskSystem, Task task) {
		this.task = task;
		this.taskSystem = taskSystem;
	}

	public TaskSystem getTaskSystem() {
		return taskSystem;
	}

	public Application getApplication() {
		return taskSystem.getApplication();
	}

	public Session getSenderSession() {
		return taskSystem.getSenderCache().get(task);
	}
}
