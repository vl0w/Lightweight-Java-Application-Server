package ljas.commons.threading.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.environment.TaskSystemImpl;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.threading.BackgroundThread;
import ljas.commons.threading.TaskExecutorThread;
import ljas.commons.threading.ThreadSystem;

import org.junit.Before;
import org.junit.Test;

public class ThreadFactoryTest {

	private TaskMonitor taskMonitor;
	private ThreadSystem threadSystem;
	private TaskSystem taskSystem;
	private ThreadFactory factory;

	@Before
	public void setUp() {
		taskMonitor = new TaskMonitor();
		threadSystem = new ThreadSystem(taskMonitor, 2);
		taskSystem = new TaskSystemImpl(threadSystem, taskMonitor);
		factory = threadSystem.getThreadFactory();
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
		// Initialize Threads
		factory.createTaskThread(taskSystem);

		// Fake some tasks
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		taskMonitor.monitorTaskTime(task1, 5000);
		taskMonitor.monitorTaskTime(task2, 10000);

		threadSystem.pauseAll();
		TaskExecutorThread thread1 = threadSystem.getThreads(
				TaskExecutorThread.class).get(0);
		thread1.scheduleTask(task2);
		TaskExecutorThread thread2 = threadSystem.getThreads(
				TaskExecutorThread.class).get(1);
		thread1.scheduleTask(task1);

		// Run
		TaskExecutorThread pickedThread = threadSystem.getThreadFactory()
				.createTaskThread(taskSystem);

		// Asserts
		assertEquals(thread2, pickedThread);
	}
}
