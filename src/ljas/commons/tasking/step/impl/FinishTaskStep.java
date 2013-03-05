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
		switch (overallResult) {
		case SUCCESS:
			notifySuccess();
			break;
		case WARNING:
			notifyWarning();
			break;
		case ERROR:
			notifyFail();
			break;
		default:
			break;
		}
		notifyExecuted();
	}

	private TaskStepResult getOverallResult() {
		List<TaskStep> statusHistory = task.getStepHistory();

		TaskStepResult overallResult = TaskStepResult.SUCCESS;
		for (TaskStep taskStatus : statusHistory) {
			if (taskStatus.getResult() == TaskStepResult.WARNING) {
				overallResult = TaskStepResult.WARNING;
			} else if (taskStatus.getResult() == TaskStepResult.ERROR) {
				overallResult = TaskStepResult.ERROR;
				break;
			}
		}

		return overallResult;
	}

	private void notifyFail() {
		List<TaskObserver> observers = getObservers();
		for (TaskObserver observer : observers) {
			observer.notifyExecutedWithErrors(task);
		}
	}

	private void notifyWarning() {
		List<TaskObserver> observers = getObservers();
		for (TaskObserver observer : observers) {
			observer.notifyExecutedWithWarnings(task);
		}
	}

	private void notifySuccess() {
		List<TaskObserver> observers = getObservers();
		for (TaskObserver observer : observers) {
			observer.notifyExecutedWithSuccess(task);
		}
	}

	private void notifyExecuted() {
		List<TaskObserver> observers = getObservers();
		for (TaskObserver observer : observers) {
			observer.notifyExecuted(task);
		}
	}

	private List<TaskObserver> getObservers() {
		return TaskObserverManager.getInstance().getTaskObservers(task);
	}
}
