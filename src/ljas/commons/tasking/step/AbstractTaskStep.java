package ljas.commons.tasking.step;

import java.io.Serializable;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.TaskStepResult;

public abstract class AbstractTaskStep implements TaskStep, Serializable {
	private static final long serialVersionUID = 908625176942848978L;

	protected TaskStepResult result;
	private TaskException exception;

	public AbstractTaskStep() {
		result = TaskStepResult.NONE;
		this.exception = null;
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
