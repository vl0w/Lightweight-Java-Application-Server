package ljas.commons.tasking.step.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import ljas.commons.exceptions.SessionException;
import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.session.SessionStore;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;

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

		RemoteNavigationStep step = new RemoteNavigationStep(task, session);
		step.execute();

		verify(session).sendObject(task);
	}

	@Test
	public void testExecute_ExceptionThrown_TaskStepResultIsError()
			throws TaskException, SessionException {
		Task task = mock(Task.class);
		Session session = mock(Session.class);
		doThrow(SessionException.class).when(session).sendObject(task);

		RemoteNavigationStep step = new RemoteNavigationStep(task, session);
		step.execute();

		assertEquals(TaskStepResult.ERROR, step.getResult());
		verify(task).setResultMessage(anyString());
	}

}
