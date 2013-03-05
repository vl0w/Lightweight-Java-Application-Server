package ljas.commons.tasking.step.impl;

import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.observation.TaskObserver;
import ljas.commons.tasking.observation.TaskObserverManager;
import ljas.commons.tasking.step.AbstractTaskStep;
import ljas.commons.tasking.step.TaskStep;

/**
 * This class does the following things:
 * <ul>
 * <li>Notifies all {@link TaskObserver}s of the current task that it has been
 * executed</li>
 * <li>Indicates that a task has been finished and no successor exists</li>
 * </ul>
 * 
 * @author jonashansen
 * 
 */
public class FinishTaskStep extends AbstractTaskStep {
	private static final long serialVersionUID = -5695204375616939223L;
	private Task task;

	public FinishTaskStep(Task task) {
		this.task = task;
	}

	@Override
	public void execute() throws TaskException {
		TaskStepResult overallResult = getOverallResult();
		notifyObservers(overallResult);
	}

	private void notifyObservers(TaskStepResult overallResult) {
		if (overallResult == TaskStepResult.SUCCESS) {
			notifySuccess();
		} else {
			notifyFail();
		}
		notifyExecuted();
	}

	private TaskStepResult getOverallResult() {
		List<TaskStep> statusHistory = task.getStepHistory();

		TaskStepResult overallResult = TaskStepResult.SUCCESS;
		for (TaskStep taskStatus : statusHistory) {
			if (taskStatus.getResult() == TaskStepResult.ERROR) {
				overallResult = TaskStepResult.ERROR;
				break;
			}
		}
		return overallResult;
	}

	private void notifyFail() {
		List<TaskObserver> observers = TaskObserverManager.getInstance()
				.getTaskObservers(task);
		for (TaskObserver observer : observers) {
			observer.notifyFail(task);
		}
	}

	private void notifySuccess() {
		List<TaskObserver> observers = TaskObserverManager.getInstance()
				.getTaskObservers(task);
		for (TaskObserver observer : observers) {
			observer.notifySuccess(task);
		}
	}

	private void notifyExecuted() {
		List<TaskObserver> observers = TaskObserverManager.getInstance()
				.getTaskObservers(task);
		for (TaskObserver observer : observers) {
			observer.notifyExecuted(task);
		}
	}
}
