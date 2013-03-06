package ljas.commons.tasking.step;

import ljas.commons.tasking.environment.TaskSystem;

public class ExecutingContext {
	private TaskSystem taskSystem;

	public void setTaskSystem(TaskSystem taskSystem) {
		this.taskSystem = taskSystem;
	}

	public TaskSystem getTaskSystem() {
		return taskSystem;
	}
}
