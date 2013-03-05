package ljas.commons.tasking.step.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ljas.commons.exceptions.SessionException;
import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskSenderCache;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.environment.TaskSystem;

import org.junit.Test;

public class SendBackToSenderStepTest {

	@Test
	public void testExecute_SenderSessionFound_SendObject()
			throws SessionException, TaskException {
		// Mocking & Stubbing
		Task task = mock(Task.class);
		TaskSystem taskSystem = mock(TaskSystem.class);
		Session session = mock(Session.class);

		TaskSenderCache senderCache = new TaskSenderCache();
		senderCache.put(task, session);

		when(taskSystem.getSenderCache()).thenReturn(senderCache);

		// Run test
		SendBackToSenderStep step = new SendBackToSenderStep(task);
		step.setTaskSystem(taskSystem);
		step.execute();

		// Verifications
		verify(session).sendObject(task);
	}

	@Test
	public void testExecute_SenderSessionFoundAndExceptionThrown_TaskStepResultIsError()
			throws SessionException, TaskException {
		// Mocking & Stubbing
		Task task = mock(Task.class);
		TaskSystem taskSystem = mock(TaskSystem.class);
		Session session = mock(Session.class);

		TaskSenderCache senderCache = new TaskSenderCache();
		senderCache.put(task, session);

		when(taskSystem.getSenderCache()).thenReturn(senderCache);

		doThrow(SessionException.class).when(session).sendObject(task);

		// Run test
		SendBackToSenderStep step = new SendBackToSenderStep(task);
		step.setTaskSystem(taskSystem);
		step.execute();

		// Asserts & Verifications
		assertEquals(TaskStepResult.ERROR, step.getResult());
		verify(task).setResultMessage(anyString());
	}
}
