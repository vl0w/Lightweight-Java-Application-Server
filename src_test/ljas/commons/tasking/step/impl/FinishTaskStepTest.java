package ljas.commons.tasking.step.impl;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.observation.TaskObserver;
import ljas.commons.tasking.observation.TaskObserverManager;
import ljas.commons.tasking.step.ExecutingContext;
import ljas.commons.tasking.step.TaskStep;

import org.junit.Before;
import org.junit.Test;

public class FinishTaskStepTest {

	private Task task;
	private List<TaskStep> executedSteps;
	private List<TaskException> exceptions;
	private TaskObserver observer;

	@Before
	public void setUp() {
		executedSteps = new ArrayList<>();
		exceptions = new ArrayList<>();

		task = mock(Task.class);
		when(task.getStepHistory()).thenReturn(executedSteps);

		observer = mock(TaskObserver.class);
		TaskObserverManager.getInstance().add(task, observer);
	}

	@Test
	public void testGeneral_IsNotForNavigation() {
		FinishTaskStep step = new FinishTaskStep(task);
		assertFalse(step.isForNavigation());
	}

	@Test
	public void testExecute_OverallResultIsSuccess_NotifySuccessAndExecuted()
			throws TaskException {
		addStepWithResult(TaskStepResult.SUCCESS);
		addStepWithResult(TaskStepResult.SUCCESS);
		addStepWithResult(TaskStepResult.SUCCESS);

		executeStep();

		verifySuccessNotification();
		verify(observer).notifyExecuted(task);
	}

	@Test
	public void testExecute_OneStepFailed_NotifyFailAndExecuted()
			throws TaskException {
		addStepWithResult(TaskStepResult.SUCCESS);
		addStepWithError(new TaskException());
		addStepWithResult(TaskStepResult.SUCCESS);

		executeStep();

		verifyFailNotification();
		verify(observer).notifyExecuted(task);
	}

	@Test
	public void testExecute_OneStepHasWarnings_NotifyWarningAndExecuted()
			throws TaskException {
		addStepWithResult(TaskStepResult.SUCCESS);
		addStepWithResult(TaskStepResult.WARNING);
		addStepWithResult(TaskStepResult.SUCCESS);

		executeStep();

		verifyWarningNotification();
		verify(observer).notifyExecuted(task);
	}

	@Test
	public void testExecute_SeveralStepHaveWarningsAndOneStepFailed_NotifyFailAndExecuted()
			throws TaskException {
		addStepWithResult(TaskStepResult.SUCCESS);
		addStepWithResult(TaskStepResult.WARNING);
		addStepWithError(new TaskException());
		addStepWithResult(TaskStepResult.WARNING);

		executeStep();

		verifyFailNotification();
		verify(observer).notifyExecuted(task);
	}

	@Test
	public void testExecute_StepHasNoResult_NotifySuccessAndExecuted()
			throws TaskException {
		addStepWithResult(TaskStepResult.NONE);

		executeStep();

		verifySuccessNotification();
		verify(observer).notifyExecuted(task);
	}

	private void executeStep() throws TaskException {
		ExecutingContext context = new ExecutingContext();
		FinishTaskStep step = new FinishTaskStep(task);
		step.execute(context);
	}

	private void addStepWithError(TaskException exception) {
		exceptions.add(exception);
		TaskStep step = mock(TaskStep.class);
		when(step.getException()).thenReturn(exception);
		when(step.getResult()).thenReturn(TaskStepResult.ERROR);
		executedSteps.add(step);
	}

	private void addStepWithResult(TaskStepResult result) {
		if (result == TaskStepResult.ERROR) {
			throw new IllegalArgumentException(
					"Use addStepWithError(...) instead!");
		}

		TaskStep step = mock(TaskStep.class);
		when(step.getResult()).thenReturn(result);
		executedSteps.add(step);
	}

	private void verifyFailNotification() {
		verify(observer).notifyExecutedWithErrors(task, exceptions);
		verify(observer, never()).notifyExecutedWithSuccess(task);
		verify(observer, never()).notifyExecutedWithWarnings(task);
	}

	private void verifyWarningNotification() {
		verify(observer, never()).notifyExecutedWithErrors(task, exceptions);
		verify(observer, never()).notifyExecutedWithSuccess(task);
		verify(observer).notifyExecutedWithWarnings(task);
	}

	private void verifySuccessNotification() {
		verify(observer, never()).notifyExecutedWithErrors(task, exceptions);
		verify(observer).notifyExecutedWithSuccess(task);
		verify(observer, never()).notifyExecutedWithWarnings(task);
	}
}
