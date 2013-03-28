package ljas.tasking.step.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import ljas.exception.TaskException;
import ljas.tasking.Task;
import ljas.tasking.environment.TaskSystem;
import ljas.tasking.step.ExecutingContext;
import ljas.tasking.step.impl.LocalNavigationStep;

import org.junit.Test;

public class LocalNavigationStepTest {

	@Test
	public void testGeneral_IsForNavigation() {
		Task task = mock(Task.class);
		LocalNavigationStep step = new LocalNavigationStep(task);
		assertTrue(step.isForNavigation());
	}

	@Test
	public void testExecute_StepGetsDelegatedToLocalTasksystem()
			throws TaskException {
		TaskSystem taskSystem = mock(TaskSystem.class);
		Task task = mock(Task.class);

		executeStep(taskSystem, task);

		verify(taskSystem).scheduleTask(task);
	}

	private void executeStep(TaskSystem taskSystem, Task task)
			throws TaskException {
		ExecutingContext context = new ExecutingContext(taskSystem, task);

		LocalNavigationStep step = new LocalNavigationStep(task);
		step.execute(context);
	}

}
