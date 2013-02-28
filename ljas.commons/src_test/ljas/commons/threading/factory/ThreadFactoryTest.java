package ljas.commons.threading.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

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
	private ThreadFactory factory;
	private ThreadSystem threadSystem;
	private TaskMonitor taskMonitor;
	private TaskSystem taskSystem;
	private Task task;

	@Before
	public void setUp() {
		task = mock(Task.class);
		taskMonitor = mock(TaskMonitor.class);
		threadSystem = new ThreadSystem(taskMonitor, 0);
		taskSystem = new TaskSystemImpl(threadSystem, taskMonitor);
		factory = new ThreadFactory(threadSystem);
	}

	@Test
	public void testCreateBackgroundThread() {
		Runnable runnable = mock(Runnable.class);
		BackgroundThread thread = factory.createBackgroundThread(runnable);
		assertTrue(thread.isRunning());
	}

	@Test
	public void testCreateTaskThread_NoThreadsYet_CreateNewOne() {
		// Run
		TaskExecutorThread thread = factory.createTaskThread(task, taskSystem);

		// Asserts
		List<TaskExecutorThread> threads = threadSystem
				.getThreads(TaskExecutorThread.class);
		assertEquals(1, threads.size());
		assertEquals(thread, threads.get(0));
	}

	@Test
	public void testCreateTaskThread_NoStatisticsYet_CreateNewOne() {
		// Stubbing
		monitorHasStatistics(false);

		// Run
		TaskExecutorThread thread = factory.createTaskThread(task, taskSystem);

		// Asserts
		List<TaskExecutorThread> threads = threadSystem
				.getThreads(TaskExecutorThread.class);
		assertEquals(1, threads.size());
		assertEquals(thread, threads.get(0));
	}

	@Test
	public void testCreateTaskThread_SeveralThreadsAndSmallTask_PickLaziest() {
		// Initialization
		threadSystem.setMaximumTaskWorkers(5);

		createThread(2000);
		TaskExecutorThread expectedThread = createThread(500);
		createThread(1000);

		// Stubbing
		monitorHasStatistics(true);
		setAverageTimeForTask(500);

		// Run
		TaskExecutorThread pickedThread = factory.createTaskThread(task,
				taskSystem);

		// Asserts
		assertEquals(expectedThread, pickedThread);
	}

	@Test
	public void testCreateTaskThread_MaximumPossibleThreadsCreated_PickLaziest() {
		// Initialization
		threadSystem.setMaximumTaskWorkers(3);

		createThread(2000);
		TaskExecutorThread expectedThread = createThread(500);
		createThread(1000);

		// Stubbing
		monitorHasStatistics(true);
		setAverageTimeForTask(5000);

		// Run
		TaskExecutorThread pickedThread = factory.createTaskThread(task,
				taskSystem);

		// Asserts
		assertEquals(expectedThread, pickedThread);
	}

	@Test
	public void testCreateTaskThread_BigTaskAndNotMaximumPossibleThreadsCreated_CreateNewOne() {
		// Initialization
		threadSystem.setMaximumTaskWorkers(5);

		createThread(2000);
		createThread(500);
		createThread(1000);

		// Stubbing
		monitorHasStatistics(true);
		setAverageTimeForTask(5000);

		// Run
		factory.createTaskThread(task, taskSystem);

		// Asserts
		List<TaskExecutorThread> threads = threadSystem
				.getThreads(TaskExecutorThread.class);
		assertEquals(4, threads.size());
	}

	private void monitorHasStatistics(boolean hasStatistics) {
		when(taskMonitor.hasStatistics(task.getClass())).thenReturn(
				Boolean.valueOf(hasStatistics));
	}

	private void setAverageTimeForTask(long averageTime) {
		when(taskMonitor.getAverageTaskTime(task)).thenReturn(
				Long.valueOf(averageTime));
	}

	private TaskExecutorThread createThread(long estimatedExecutionTime) {
		TaskExecutorThread thread = new TaskExecutorThread(threadSystem,
				taskSystem);
		when(taskMonitor.getEstimatedExecutionTime(thread)).thenReturn(
				Long.valueOf(estimatedExecutionTime));
		return thread;
	}
}
