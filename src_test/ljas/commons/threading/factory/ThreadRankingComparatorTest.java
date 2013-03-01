package ljas.commons.threading.factory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.threading.TaskExecutorThread;

import org.junit.Test;

public class ThreadRankingComparatorTest {
	@Test
	public void testCompareWithThreadList() {
		// Mocking & Stubbing
		TaskExecutorThread thread1 = mock(TaskExecutorThread.class);
		TaskExecutorThread thread2 = mock(TaskExecutorThread.class);
		TaskMonitor taskMonitor = mock(TaskMonitor.class);

		when(taskMonitor.getEstimatedExecutionTime(thread1)).thenReturn(
				Long.valueOf(10000));
		when(taskMonitor.getEstimatedExecutionTime(thread2)).thenReturn(
				Long.valueOf(5000));

		// Create and sort list
		List<TaskExecutorThread> threads = new ArrayList<>();
		threads.add(thread1);
		threads.add(thread2);

		ThreadRankingComparator comparator = new ThreadRankingComparator(
				taskMonitor);
		Collections.sort(threads, comparator);

		// Asserts
		assertEquals("Thread2 is expected to be in first position in List",
				thread2, threads.get(0));
		assertEquals("Thread2 is expected to be in second position in List",
				thread1, threads.get(1));
	}
}
