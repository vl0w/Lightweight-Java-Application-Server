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
import ljas.commons.tasking.TaskStateResult;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.environment.TaskSystemImpl;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.tasking.status.StateFactory;
import ljas.commons.tasking.status.TaskState;
import ljas.commons.tasking.status.navigator.TaskNavigator;

import org.junit.Before;
import org.junit.Test;

public class TaskExecutorThreadTest {

	private Task task;
	private TaskState currentState;
	private StateFactory stateFactory;
	private TaskState successorState;
	private TaskNavigator successorNavigator;
	private TaskMonitor taskMonitor;
	private List<TaskState> taskStateHistory;

	@Before
	public void initMocksAndStubs() throws Exception {
		// Initial Mocking
		task = mock(Task.class);
		currentState = mock(TaskState.class);
		successorState = mock(TaskState.class);
		stateFactory = mock(StateFactory.class);
		successorNavigator = mock(TaskNavigator.class);
		taskMonitor = mock(TaskMonitor.class);
		taskStateHistory = new ArrayList<>();

		// Initial Stubbing
		when(task.getCurrentState()).thenReturn(currentState);
		when(task.getStateFactory()).thenReturn(stateFactory);
		when(stateFactory.nextStatus(task, currentState)).thenReturn(
				successorState);
		when(successorState.getNavigator()).thenReturn(successorNavigator);
		when(task.getStateHistory()).thenReturn(taskStateHistory);
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
	public void testRunCycle_TaskFoundAndNoExceptionOccurs_VerifyExecutionAndNavigation()
			throws TaskException {
		// Setup thread
		ThreadSystem threadSystem = new ThreadSystem(taskMonitor, 0);
		TaskSystem taskSystem = new TaskSystemImpl(threadSystem, taskMonitor);
		TaskExecutorThread thread = createTaskExecutorThread(threadSystem,
				taskSystem);
		thread.getTaskQueue().add(task);

		// Run test
		thread.runCycle();

		// Verifications
		verify(currentState).setTaskSystem(taskSystem);
		verify(currentState).execute();
		verify(successorState).setTaskSystem(taskSystem);
		verify(task).setCurrentState(successorState);
		verify(successorNavigator).navigate(task);

		verify(taskMonitor).monitorTaskTime(eq(task), anyLong());

		// Asserts
		assertTrue(taskStateHistory.contains(currentState));
	}

	@Test
	public void testRunCycle_TaskStateHasNoResult_SuccessAsDefault() {
		// Mocking & Stubbing
		when(currentState.getResult()).thenReturn(TaskStateResult.NONE);

		// Setup thread
		TaskExecutorThread thread = createTaskExecutorThread();
		thread.getTaskQueue().add(task);

		// Run test
		thread.runCycle();

		// Verification
		verify(currentState).setResult(TaskStateResult.SUCCESS);
	}

	@Test
	public void testRunCycle_TaskStateThrowsException_StateResultIsErrorWithExceptionMessage()
			throws TaskException {
		// Initialization
		final String message = "Some exceptio message";
		TaskException expectedException = new TaskException(message);

		// Mocking & Stubbing
		doThrow(expectedException).when(currentState).execute();

		// Setup thread
		TaskExecutorThread thread = createTaskExecutorThread();
		thread.getTaskQueue().add(task);

		// Run test
		thread.runCycle();

		// Verifications
		verify(currentState).setResult(TaskStateResult.ERROR);
		verify(task).setResultMessage(message);
	}

	@Test
	public void testRunCycle_TaskHasNoCurrentState_StateFactoryCreatesInitialState() {
		// Mocking & Stubbing
		when(task.getCurrentState()).thenReturn(null);

		// Setup thread
		TaskExecutorThread thread = createTaskExecutorThread();
		thread.getTaskQueue().add(task);

		// Run test
		thread.runCycle();

		// Verifications
		verify(stateFactory).nextStatus(task, null);
	}

	@Test
	public void testRunCycle_NoTaskAndThreadWasTooLongIdle_ThreadGetsKilled()
			throws InterruptedException {
		// Mocking & Stubbing
		ThreadSystem threadSystem = mock(ThreadSystem.class);
		when(threadSystem.getDefaultThreadDelay()).thenReturn(
				Integer.valueOf(1));
		when(threadSystem.getTimeForTaskExecutorThreadToSelfDestruction())
				.thenReturn(Long.valueOf(5));

		// Setup thread
		TaskSystem taskSystem = new TaskSystemImpl(threadSystem, taskMonitor);
		TaskExecutorThread thread = createTaskExecutorThread(threadSystem,
				taskSystem);

		// Run test
		for (int i = 0; i < threadSystem
				.getTimeForTaskExecutorThreadToSelfDestruction()
				/ threadSystem.getDefaultThreadDelay(); i++) {
			thread.runCycle();
		}

		// Asserts
		assertTrue("Thread is expected to be killed", thread.isKilled());
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
