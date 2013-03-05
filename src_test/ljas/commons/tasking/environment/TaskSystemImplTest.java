package ljas.commons.tasking.environment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.List;

import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.threading.TaskExecutorThread;
import ljas.commons.threading.ThreadSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskSystemImplTest {

	private ThreadSystem threadSystem;

	@Before
	public void setUpThreadSystem() {
		threadSystem = new ThreadSystem(1);
	}

	@After
	public void tearDownThreadSystem() {
		threadSystem.killAll();
	}

	@Test
	public void testScheduleTask_NoSession_DelegateTaskToThreadSystem() {
		// Initialization
		Task task = mock(Task.class);
		TaskSystem taskSystem = new TaskSystemImpl(threadSystem);

		// Pause threads
		threadSystem.getThreadFactory().createTaskThread(taskSystem);
		threadSystem.pauseAll();

		// Run test
		taskSystem.scheduleTask(task);

		// Asserts
		List<TaskExecutorThread> taskExecutors = threadSystem
				.getThreads(TaskExecutorThread.class);
		assertEquals(1, taskExecutors.size());
		assertEquals(1, taskExecutors.get(0).getTaskQueue().size());
		assertEquals(task, taskExecutors.get(0).getTaskQueue().remove());
	}

	@Test
	public void testScheduleTask_WithSession_PutSessionToSenderCache() {
		// Initialization
		Session session = mock(Session.class);
		Task task = mock(Task.class);
		TaskSystem taskSystem = new TaskSystemImpl(threadSystem);

		// Run test
		taskSystem.scheduleTask(task, session);

		// Asserts
		assertEquals(session, taskSystem.getSenderCache().remove(task));
	}

	@Test
	public void testAddBackgroundTask_DelegateTaskToThreadSystem() {
		// Initialization
		Task task = mock(Task.class);
		TaskSystem taskSystem = new TaskSystemImpl(threadSystem);

		// Run test
		taskSystem.scheduleTask(task);

		// Asserts
		List<TaskExecutorThread> taskExecutors = threadSystem
				.getThreads(TaskExecutorThread.class);
		assertEquals(1, taskExecutors.size());
		assertEquals(1, taskExecutors.get(0).getTaskQueue().size());
		assertEquals(task, taskExecutors.get(0).getTaskQueue().remove());
	}
}
