package ljas.commons.tasking.status.impl;

import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStateResult;
import ljas.commons.tasking.observation.TaskObserver;
import ljas.commons.tasking.observation.TaskObserverFactory;
import ljas.commons.tasking.status.AbstractTaskState;
import ljas.commons.tasking.status.TaskState;
import ljas.commons.tasking.status.navigator.LocalNavigator;
import ljas.commons.tasking.status.navigator.TaskNavigator;

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
public class FinishedState extends AbstractTaskState {
	private static final long serialVersionUID = -5695204375616939223L;

	public FinishedState(Task task) {
		super(task);
	}

	@Override
	public void execute() throws TaskException {
		TaskStateResult overallResult = getOverallResult();
		notifyObservers(overallResult);
	}

	@Override
	public TaskNavigator getNavigator() {
		return new LocalNavigator();
	}

	private void notifyObservers(TaskStateResult overallResult) {
		if (overallResult == TaskStateResult.SUCCESS) {
			notifySuccess();
		} else {
			notifyFail();
		}
		notifyExecuted();
	}

	private TaskStateResult getOverallResult() {
		List<TaskState> statusHistory = task.getStateHistory();

		TaskStateResult overallResult = TaskStateResult.SUCCESS;
		for (TaskState taskStatus : statusHistory) {
			if (taskStatus.getResult() == TaskStateResult.ERROR) {
				overallResult = TaskStateResult.ERROR;
				break;
			}
		}
		return overallResult;
	}

	private void notifyFail() {
		List<TaskObserver> observers = TaskObserverFactory.getInstance()
				.getTaskObservers(task);
		for (TaskObserver observer : observers) {
			observer.notifyFail(task);
		}
	}

	private void notifySuccess() {
		List<TaskObserver> observers = TaskObserverFactory.getInstance()
				.getTaskObservers(task);
		for (TaskObserver observer : observers) {
			observer.notifySuccess(task);
		}
	}

	private void notifyExecuted() {
		List<TaskObserver> observers = TaskObserverFactory.getInstance()
				.getTaskObservers(task);
		for (TaskObserver observer : observers) {
			observer.notifyExecuted(task);
		}
	}
}
