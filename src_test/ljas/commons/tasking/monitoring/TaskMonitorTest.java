package ljas.commons.tasking.monitoring;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ljas.commons.tasking.Task;
import ljas.commons.threading.TaskExecutorThread;

import org.junit.Before;
import org.junit.Test;

public class TaskMonitorTest {

	private TaskMonitor monitor;
	private TaskDefToBeMonitored monitoredTask;

	@Before
	public void setUp() {
		monitor = new TaskMonitor();

		monitoredTask = mock(TaskDefToBeMonitored.class);
		monitor.monitorTaskTime(monitoredTask, 10000);
		monitor.monitorTaskTime(monitoredTask, 5000);
	}

	@Test
	public void testGetAverageTaskTime_WithMonitorings_MustReturnAverage() {
		assertEquals(7500, monitor.getAverageTaskTime(monitoredTask));
	}

	@Test
	public void testGetAverageTaskTime_WithoutMonitorings_MustReturnZero() {
		Task task = mock(Task.class);
		assertEquals(0, monitor.getAverageTaskTime(task));
	}

	@Test
	public void testGetEstimatedExecutionTime_ListContainsTasks_MustReturnTotalAmount() {
		List<Task> tasks = new ArrayList<>();
		tasks.add(monitoredTask);
		tasks.add(monitoredTask);
		assertEquals(15000, monitor.getEstimatedExecutionTime(tasks));
	}

	@Test
	public void testGetEstimatedExecutionTime_ListIsEmpty_MustReturnZero() {
		List<Task> tasks = new ArrayList<>();
		assertEquals(0, monitor.getEstimatedExecutionTime(tasks));
	}

	@Test
	public void testGetEstimatedExecutionTime_WithThread_MustReturnTotalAmountOfAllTasks() {
		Queue<Task> tasks = new LinkedList<>();
		tasks.add(monitoredTask);
		tasks.add(monitoredTask);
		tasks.add(monitoredTask);
		tasks.add(monitoredTask);
		TaskExecutorThread thread = mock(TaskExecutorThread.class);
		when(thread.getTaskQueue()).thenReturn(tasks);

		assertEquals(30000, monitor.getEstimatedExecutionTime(thread));
	}

	private abstract class TaskDefToBeMonitored extends Task {

		private static final long serialVersionUID = 1L;

	}

}
