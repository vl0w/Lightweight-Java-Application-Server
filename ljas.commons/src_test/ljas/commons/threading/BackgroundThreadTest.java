package ljas.commons.threading;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;
import ljas.commons.tasking.monitoring.TaskMonitor;

import org.junit.Test;

public class BackgroundThreadTest extends TestCase {

	@Test
	public void testRunCycle_TaskPerformed() throws Exception {
		// Mocking & Stubbing
		Runnable task = mock(Runnable.class);

		// Run
		createBackgroundThread(task).runCycle();

		// Verifications
		verify(task).run();
	}

	private BackgroundThread createBackgroundThread(Runnable runnable) {
		ThreadSystem threadSystem = new ThreadSystem(new TaskMonitor(), 0);
		return new BackgroundThread(threadSystem, runnable);
	}
}
