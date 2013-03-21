package ljas.commons.threading.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import ljas.commons.application.Application;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.environment.TaskSystemImpl;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.threading.BackgroundThread;
import ljas.commons.threading.TaskExecutorThread;
import ljas.commons.threading.ThreadSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ThreadFactoryTest {

	private TaskMonitor taskMonitor;
	private ThreadSystem threadSystem;
	private TaskSystem taskSystem;
	private ThreadFactory factory;

	@Before
	public void setUp() {
		threadSystem = new ThreadSystem(2);
		taskMonitor = new TaskMonitor();
		Application application = mock(Application.class);
		taskSystem = new TaskSystemImpl(threadSystem, application,
				taskMonitor);
		factory = threadSystem.getThreadFactory();
	}

	@After
	public void tearDown() {
		threadSystem.killAll();
	}

	@Test
	public void testCreateBackgroundThread() {
		Runnable runnable = mock(Runnable.class);
		BackgroundThread thread = factory.createBackgroundThread(runnable);
		assertTrue(thread.isRunning());
	}

	@Test
	public void testCreateTaskThread_NoThreadsYet_CreateNewOnes() {
		// Run
		factory.createTaskThread(taskSystem);

		// Asserts
		assertEquals(2, threadSystem.getThreads(TaskExecutorThread.class)
				.size());
	}

	@Test
	public void testCreateTaskThread_SeverallThreads_PickLaziest() {
		// Initialize threads
		factory.createTaskThread(taskSystem);

		// Fake some tasks
		Task task1 = mock(TaskOne.class);
		Task task2 = mock(TaskTwo.class);
		taskMonitor.monitorTaskTime(task1, 5000);
		taskMonitor.monitorTaskTime(task2, 10000);

		threadSystem.pauseAll();
		TaskExecutorThread thread1 = threadSystem.getThreads(
				TaskExecutorThread.class).get(0);
		thread1.scheduleTask(task2);
		TaskExecutorThread thread2 = threadSystem.getThreads(
				TaskExecutorThread.class).get(1);
		thread2.scheduleTask(task1);

		// Run
		TaskExecutorThread pickedThread = factory.createTaskThread(taskSystem);

		// Asserts
		assertEquals(thread2, pickedThread);
	}

	@Test
	public void testCreateTaskThread_KillOne_NewOneGetsCreated() {
		// Initialize threads
		factory.createTaskThread(taskSystem);

		// Kill thread
		TaskExecutorThread thread = factory.createTaskThread(taskSystem);
		thread.kill();

		// Recreate threads
		factory.createTaskThread(taskSystem);

		// Asserts
		assertFalse(threadSystem.getThreads(TaskExecutorThread.class).contains(
				thread));
	}

	private abstract class TaskOne extends Task {

		private static final long serialVersionUID = 1L;

	}

	private abstract class TaskTwo extends Task {

		private static final long serialVersionUID = 1L;

	}
}
