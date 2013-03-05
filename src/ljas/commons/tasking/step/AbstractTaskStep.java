package ljas.commons.tasking.step;

import java.io.Serializable;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.environment.TaskSystem;

public abstract class AbstractTaskStep implements TaskStep, Serializable {
	private static final long serialVersionUID = 908625176942848978L;

	private transient TaskSystem taskSystem;
	protected TaskStepResult result;
	private TaskException exception;

	public AbstractTaskStep() {
		result = TaskStepResult.NONE;
		this.exception = null;
	}

	@Override
	public void setTaskSystem(TaskSystem taskSystem) {
		this.taskSystem = taskSystem;
	}

	@Override
	public TaskSystem getTaskSystem() {
		return taskSystem;
	}

	@Override
	public TaskStepResult getResult() {
		return result;
	}

	@Override
	public void setResult(TaskStepResult result) {
		this.result = result;
	}

	@Override
	public TaskException getException() {
		return exception;
	}

	@Override
	public void setException(TaskException exception) {
		this.exception = exception;
	}

	@Override
	public boolean isForNavigation() {
		return false;
	}

}
