package ljas.tasking.environment;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import ljas.session.Session;
import ljas.tasking.Task;
import ljas.tasking.environment.TaskSystem;
import ljas.tasking.environment.TaskSystemSessionDataObserver;

import org.junit.Test;

public class TaskSystemSessionDataObserverTest {

	@Test
	public void testNotifyObjectReceived_IsATask_DelegateToTaskSystem() {
		// Mocking & Stubbing
		TaskSystem taskSystem = mock(TaskSystem.class);
		Session session = mock(Session.class);
		Task task = mock(Task.class);

		// Run test
		TaskSystemSessionDataObserver observer = new TaskSystemSessionDataObserver(
				taskSystem);
		observer.onObjectReceived(session, task);

		// Verifications
		verify(taskSystem).scheduleTask(task, session);
	}

	@Test
	public void testNotifyObjectReceived_IsNotATask_DoNotDelegateToTaskSystem() {
		// Mocking & Stubbing
		TaskSystem taskSystem = mock(TaskSystem.class);
		Session session = mock(Session.class);
		Object sendedObject = new Object();

		// Run test
		TaskSystemSessionDataObserver observer = new TaskSystemSessionDataObserver(
				taskSystem);
		observer.onObjectReceived(session, sendedObject);

		// Verifications
		verify(taskSystem, never()).scheduleTask(any(Task.class), eq(session));
	}
}
