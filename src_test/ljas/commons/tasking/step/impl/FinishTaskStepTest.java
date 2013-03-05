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
import ljas.commons.tasking.step.TaskStep;

import org.junit.Before;
import org.junit.Test;

public class FinishTaskStepTest {

	private Task task;
	private List<TaskStep> executedSteps;
	private TaskObserver observer;

	@Before
	public void setUp() {
		executedSteps = new ArrayList<>();

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

		FinishTaskStep step = new FinishTaskStep(task);
		step.execute();

		verifySuccessNotification();
		verify(observer).notifyExecuted(task);
	}

	@Test
	public void testExecute_OneStepFailed_NotifyFailAndExecuted()
			throws TaskException {
		addStepWithResult(TaskStepResult.SUCCESS);
		addStepWithResult(TaskStepResult.ERROR);
		addStepWithResult(TaskStepResult.SUCCESS);

		FinishTaskStep step = new FinishTaskStep(task);
		step.execute();

		verifyFailNotification();
		verify(observer).notifyExecuted(task);
	}

	@Test
	public void testExecute_OneStepHasWarnings_NotifyWarningAndExecuted()
			throws TaskException {
		addStepWithResult(TaskStepResult.SUCCESS);
		addStepWithResult(TaskStepResult.WARNING);
		addStepWithResult(TaskStepResult.SUCCESS);

		FinishTaskStep step = new FinishTaskStep(task);
		step.execute();

		verifyWarningNotification();
		verify(observer).notifyExecuted(task);
	}

	@Test
	public void testExecute_SeveralStepHaveWarningsAndOneStepFailed_NotifyFailAndExecuted()
			throws TaskException {
		addStepWithResult(TaskStepResult.SUCCESS);
		addStepWithResult(TaskStepResult.WARNING);
		addStepWithResult(TaskStepResult.ERROR);
		addStepWithResult(TaskStepResult.WARNING);

		FinishTaskStep step = new FinishTaskStep(task);
		step.execute();

		verifyFailNotification();
		verify(observer).notifyExecuted(task);
	}

	@Test
	public void testExecute_StepHasNoResult_NotifySuccessAndExecuted()
			throws TaskException {
		addStepWithResult(TaskStepResult.NONE);

		FinishTaskStep step = new FinishTaskStep(task);
		step.execute();

		verifySuccessNotification();
		verify(observer).notifyExecuted(task);
	}

	private void addStepWithResult(TaskStepResult result) {
		TaskStep step = mock(TaskStep.class);
		when(step.getResult()).thenReturn(result);
		executedSteps.add(step);
	}

	private void verifyFailNotification() {
		verify(observer).notifyExecutedWithErrors(task);
		verify(observer, never()).notifyExecutedWithSuccess(task);
		verify(observer, never()).notifyExecutedWithWarnings(task);
	}

	private void verifyWarningNotification() {
		verify(observer, never()).notifyExecutedWithErrors(task);
		verify(observer, never()).notifyExecutedWithSuccess(task);
		verify(observer).notifyExecutedWithWarnings(task);
	}

	private void verifySuccessNotification() {
		verify(observer, never()).notifyExecutedWithErrors(task);
		verify(observer).notifyExecutedWithSuccess(task);
		verify(observer, never()).notifyExecutedWithWarnings(task);
	}
}
