package ljas.tasking.step;

import ljas.application.Application;
import ljas.session.Session;
import ljas.tasking.Task;
import ljas.tasking.environment.TaskSystem;

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
