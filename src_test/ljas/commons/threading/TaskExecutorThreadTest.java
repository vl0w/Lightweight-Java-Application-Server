package ljas.commons.threading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.environment.TaskSystemImpl;
import ljas.commons.tasking.flow.TaskFlow;
import ljas.commons.tasking.flow.TaskFlowBuilder;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.tasking.step.TaskStep;

import org.junit.Before;
import org.junit.Test;

public class TaskExecutorThreadTest {

	private Task task;
	private TaskMonitor taskMonitor;
	private List<TaskStep> taskStepHistory;
	private TaskFlow taskFlow;
	private TaskStep step;

	@Before
	public void initMocksAndStubs() throws Exception {
		// Initial Mocking
		task = mock(Task.class);
		taskMonitor = mock(TaskMonitor.class);
		taskStepHistory = new ArrayList<>();
		step = mock(TaskStep.class);
		taskFlow = new TaskFlowBuilder(task).perform(step).build();

		// Initial Stubbing
		when(task.getTaskFlow()).thenReturn(taskFlow); // TODO
		when(task.getStepHistory()).thenReturn(taskStepHistory);
	}

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
		// Setup thread
		TaskExecutorThread thread = createTaskExecutorThread();

		// Asserts
		assertTrue(thread.scheduleTask(task));
		assertEquals(task, thread.getTaskQueue().remove());
	}

	@Test
	public void testRunCycle_ThreeStepsAndOneNavigatorFound_VerifyExecutionAndNavigation()
			throws TaskException {
		// Initialize TaskFlow
		TaskStep step1 = mock(TaskStep.class);
		TaskStep step2 = mock(TaskStep.class);
		TaskStep step3 = mock(TaskStep.class);

		when(step1.isForNavigation()).thenReturn(Boolean.FALSE);
		when(step2.isForNavigation()).thenReturn(Boolean.TRUE);
		when(step3.isForNavigation()).thenReturn(Boolean.FALSE);

		taskFlow = new TaskFlowBuilder(task).perform(step1).perform(step2)
				.perform(step3).build();
		when(task.getTaskFlow()).thenReturn(taskFlow);

		// Setup thread
		ThreadSystem threadSystem = new ThreadSystem(taskMonitor, 0);
		TaskSystem taskSystem = new TaskSystemImpl(threadSystem, taskMonitor);
		TaskExecutorThread thread = createTaskExecutorThread(threadSystem,
				taskSystem);
		thread.getTaskQueue().add(task);

		// Run test
		thread.runCycle();

		// Verifications
		verify(step1).setTaskSystem(taskSystem);
		verify(step1).execute();
		verify(step2).setTaskSystem(taskSystem);
		verify(step2).execute();
		verify(taskMonitor).monitorTaskTime(eq(task), anyLong());

		// Asserts
		assertTrue(taskStepHistory.contains(step1));
		assertTrue(taskStepHistory.contains(step2));
	}

	@Test
	public void testRunCycle_TaskStateHasNoResult_SuccessAsDefault() {
		// Mocking & Stubbing
		when(step.getResult()).thenReturn(TaskStepResult.NONE);

		// Setup thread
		TaskExecutorThread thread = createTaskExecutorThread();
		thread.getTaskQueue().add(task);

		// Run test
		thread.runCycle();

		// Verification
		verify(step).setResult(TaskStepResult.SUCCESS);
	}

	@Test
	public void testRunCycle_TaskStateThrowsException_StateResultIsErrorWithExceptionMessage()
			throws TaskException {
		// Initialization
		final String message = "Some exceptio message";
		TaskException expectedException = new TaskException(message);

		// Mocking & Stubbing
		doThrow(expectedException).when(step).execute();

		// Setup thread
		TaskExecutorThread thread = createTaskExecutorThread();
		thread.getTaskQueue().add(task);

		// Run test
		thread.runCycle();

		// Verifications
		verify(step).setResult(TaskStepResult.ERROR);
		verify(task).setResultMessage(message);
	}

	private TaskExecutorThread createTaskExecutorThread() {
		ThreadSystem threadSystem = new ThreadSystem(taskMonitor, 0);
		TaskSystem taskSystem = new TaskSystemImpl(threadSystem, taskMonitor);
		return createTaskExecutorThread(threadSystem, taskSystem);
	}

	private TaskExecutorThread createTaskExecutorThread(
			ThreadSystem threadSystem, TaskSystem taskSystem) {
		TaskExecutorThread thread = new TaskExecutorThread(threadSystem,
				taskSystem);
		thread.pause();
		return thread;
	}
}
