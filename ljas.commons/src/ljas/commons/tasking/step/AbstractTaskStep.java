package ljas.commons.tasking.step;

import java.io.Serializable;

import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.environment.TaskSystem;

public abstract class AbstractTaskStep implements TaskStep, Serializable {
	private static final long serialVersionUID = 908625176942848978L;

	protected transient TaskSystem taskSystem;
	protected TaskStepResult result;

	public AbstractTaskStep() {
		result = TaskStepResult.NONE;
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
	public boolean isForNavigation() {
		return false;
	}

}
