package ljas.tasking.step.impl;

import java.util.ArrayList;
import java.util.List;

import ljas.exception.TaskException;
import ljas.tasking.Task;
import ljas.tasking.TaskStepResult;
import ljas.tasking.observation.TaskObserver;
import ljas.tasking.observation.TaskObserverManager;
import ljas.tasking.step.AbstractTaskStep;
import ljas.tasking.step.ExecutingContext;
import ljas.tasking.step.TaskStep;

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
	public void execute(ExecutingContext context) throws TaskException {
		TaskStepResult overallResult = getOverallResult();
		notifyObservers(overallResult);
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

	private void notifyObservers(TaskStepResult result) {
		switch (result) {
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

	private void notifyFail() {
		List<TaskObserver<Task>> observers = getObservers();
		List<TaskException> exceptions = collectAllExceptions();
		for (TaskObserver<Task> observer : observers) {
			observer.notifyExecutedWithErrors(task, exceptions);
		}
	}

	private void notifyWarning() {
		List<TaskObserver<Task>> observers = getObservers();
		for (TaskObserver<Task> observer : observers) {
			observer.notifyExecutedWithWarnings(task);
		}
	}

	private void notifySuccess() {
		List<TaskObserver<Task>> observers = getObservers();
		for (TaskObserver<Task> observer : observers) {
			observer.notifyExecutedWithSuccess(task);
		}
	}

	private void notifyExecuted() {
		List<TaskObserver<Task>> observers = getObservers();
		for (TaskObserver<Task> observer : observers) {
			observer.notifyExecuted(task);
		}
	}

	private List<TaskException> collectAllExceptions() {
		List<TaskException> exceptions = new ArrayList<>();

		for (TaskStep step : task.getStepHistory()) {
			TaskException exception = step.getException();
			if (exception != null) {
				exceptions.add(exception);
			}
		}

		return exceptions;
	}

	private List<TaskObserver<Task>> getObservers() {
		return TaskObserverManager.getInstance().getObservers(task)
				.castTo(Task.class);
	}
}
