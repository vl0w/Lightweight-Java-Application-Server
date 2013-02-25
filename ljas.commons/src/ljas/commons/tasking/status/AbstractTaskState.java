package ljas.commons.tasking.status;

import java.io.Serializable;

import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStateResult;
import ljas.commons.tasking.environment.TaskSystem;

public abstract class AbstractTaskState implements TaskState, Serializable {
	private static final long serialVersionUID = 908625176942848978L;

	protected Task task;
	protected transient TaskSystem taskSystem;
	protected TaskStateResult result;

	public AbstractTaskState(Task task) {
		this.task = task;
	}

	@Override
	public void setTaskSystem(TaskSystem taskSystem) {
		this.taskSystem = taskSystem;
	}

	public TaskSystem getTaskSystem() {
		return taskSystem;
	}

	@Override
	public TaskStateResult getResult() {
		return result;
	}

	@Override
	public void setResult(TaskStateResult result) {
		this.result = result;
	}

	/**
	 * Serializes when it has to be sended to a session
	 */
	protected void serialize() {
		taskSystem = null;
	}

}
