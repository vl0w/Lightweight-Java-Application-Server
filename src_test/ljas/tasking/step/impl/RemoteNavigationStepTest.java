package ljas.tasking.step.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import ljas.exception.SessionException;
import ljas.exception.TaskException;
import ljas.session.Session;
import ljas.session.SessionStore;
import ljas.tasking.Task;
import ljas.tasking.TaskStepResult;
import ljas.tasking.environment.TaskSystem;
import ljas.tasking.step.ExecutingContext;
import ljas.tasking.step.impl.LocalNavigationStep;
import ljas.tasking.step.impl.RemoteNavigationStep;

import org.junit.Test;

public class RemoteNavigationStepTest {

	@Test
	public void testGeneral_IsForNavigation() {
		Task task = mock(Task.class);
		LocalNavigationStep step = new LocalNavigationStep(task);
		assertTrue(step.isForNavigation());
	}

	@Test
	public void testInit_SessionHashcodeIsInSessionStore() {
		Task task = mock(Task.class);
		Session session = mock(Session.class);

		new RemoteNavigationStep(task, session);

		assertNotNull(SessionStore.findSession(session.hashCode()));
	}

	@Test
	public void testExecute_NoExceptionThrown_TaskSended()
			throws TaskException, SessionException {
		Task task = mock(Task.class);
		Session session = mock(Session.class);

		RemoteNavigationStep step = executeStep(task, session);

		verify(session).sendObject(task);
		assertNull(step.getException());
	}

	@Test
	public void testExecute_ExceptionThrown_TaskStepResultIsError()
			throws TaskException, SessionException {
		Task task = mock(Task.class);
		Session session = mock(Session.class);

		SessionException expectedException = new SessionException();
		doThrow(expectedException).when(session).sendObject(task);

		RemoteNavigationStep step = executeStep(task, session);

		assertEquals(TaskStepResult.ERROR, step.getResult());
		assertEquals(expectedException, step.getException().getCause());
	}

	private RemoteNavigationStep executeStep(Task task, Session session)
			throws TaskException {
		TaskSystem taskSystem = mock(TaskSystem.class);
		ExecutingContext context = new ExecutingContext(taskSystem, task);
		RemoteNavigationStep step = new RemoteNavigationStep(task, session);
		step.execute(context);
		return step;
	}

}
