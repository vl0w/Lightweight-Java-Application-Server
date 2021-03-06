package ljas.tasking.step.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import ljas.exception.SessionException;
import ljas.exception.TaskException;
import ljas.session.Session;
import ljas.tasking.Task;
import ljas.tasking.TaskStepResult;
import ljas.tasking.environment.TaskSystem;
import ljas.tasking.step.ExecutingContext;
import ljas.tasking.step.impl.LocalNavigationStep;
import ljas.tasking.step.impl.SendBackToSenderStep;

import org.junit.Test;

public class SendBackToSenderStepTest {

	@Test
	public void testGeneral_IsForNavigation() {
		Task task = mock(Task.class);
		LocalNavigationStep step = new LocalNavigationStep(task);
		assertTrue(step.isForNavigation());
	}

	@Test
	public void testExecute_SenderSessionFound_SendObject()
			throws SessionException, TaskException {
		// Mocking & Stubbing
		Task task = mock(Task.class);
		TaskSystem taskSystem = mock(TaskSystem.class);
		Session session = mock(Session.class);

		Map<Task, Session> senderCacheMap = new HashMap<>();
		senderCacheMap.put(task, session);

		when(taskSystem.getSenderCache()).thenReturn(senderCacheMap);

		// Run test
		executeStep(task, taskSystem);

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

		Map<Task, Session> senderCacheMap = new HashMap<>();
		senderCacheMap.put(task, session);

		when(taskSystem.getSenderCache()).thenReturn(senderCacheMap);

		SessionException expectedException = new SessionException();
		doThrow(expectedException).when(session).sendObject(task);

		// Run test
		SendBackToSenderStep step = executeStep(task, taskSystem);

		// Asserts & Verifications
		assertEquals(TaskStepResult.ERROR, step.getResult());
		assertEquals(expectedException, step.getException().getCause());
	}

	private SendBackToSenderStep executeStep(Task task, TaskSystem taskSystem)
			throws TaskException {
		ExecutingContext context = new ExecutingContext(taskSystem, task);

		SendBackToSenderStep step = new SendBackToSenderStep(task);
		step.execute(context);

		return step;
	}
}
