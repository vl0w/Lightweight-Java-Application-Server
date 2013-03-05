package ljas.commons.tasking.step.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;

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

		LocalNavigationStep step = new LocalNavigationStep(task);
		step.setTaskSystem(taskSystem);
		step.execute();

		verify(taskSystem).scheduleTask(task);
	}
}
