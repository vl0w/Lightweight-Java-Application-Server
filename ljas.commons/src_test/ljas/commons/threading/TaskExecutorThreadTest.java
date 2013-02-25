package ljas.commons.threading;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.environment.TaskSystemImpl;
import ljas.commons.tasking.monitoring.TaskMonitor;

import org.junit.Test;

public class TaskExecutorThreadTest extends TestCase {

	@Test
	public void testScheduleTask_ThreadIsKilled_TaskWontBeScheduled() {
		// Mocking & Stubbing
		Task task = mock(Task.class);

		// Setup thread
		TaskExecutorThread thread = createTaskExecutorThread();
		thread.kill();

		// Asserts
		assertFalse(thread.scheduleTask(task));
	}

	@Test
	public void testScheduleTask_ThreadIsNotKilled_TaskGetScheduled() {
		// Mocking & Stubbing
		Task task = mock(Task.class);

		// Setup thread
		TaskExecutorThread thread = createTaskExecutorThread();

		// Asserts
		assertTrue(thread.scheduleTask(task));
		assertEquals(task, thread.getTaskQueue().remove());
	}

	@Test
	public void testRunCycle() {

	}

	public void testMORE() {
		fail("Much more to test!");
	}

	private TaskExecutorThread createTaskExecutorThread() {
		TaskMonitor taskMonitor = mock(TaskMonitor.class);
		ThreadSystem threadSystem = new ThreadSystem(taskMonitor, 0);
		TaskSystem taskSystem = new TaskSystemImpl(threadSystem, taskMonitor);
		return new TaskExecutorThread(threadSystem, taskSystem);
	}
}
